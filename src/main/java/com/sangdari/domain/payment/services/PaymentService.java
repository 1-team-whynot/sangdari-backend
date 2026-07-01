package com.sangdari.domain.payment.services;

import com.sangdari.domain.payment.clients.TossPaymentClient;
import com.sangdari.domain.payment.entities.Payment;
import com.sangdari.domain.payment.entities.PaymentReadyInfo;
import com.sangdari.domain.payment.mappers.PaymentMapper;
import com.sangdari.domain.payment.requests.PaymentConfirmRequest;
import com.sangdari.domain.payment.requests.PaymentMockConfirmRequest;
import com.sangdari.domain.payment.requests.PaymentReadyRequest;
import com.sangdari.domain.payment.responses.PaymentConfirmResponse;
import com.sangdari.domain.payment.responses.PaymentReadyMenuResponse;
import com.sangdari.domain.payment.responses.PaymentReadyResponse;
import com.sangdari.domain.payment.responses.TossConfirmResponse;
import com.sangdari.global.exception.custom.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final PaymentMapper paymentMapper;
    private final TossPaymentClient tossPaymentClient;

    @Transactional
    public PaymentReadyResponse ready(Long userId, PaymentReadyRequest request) {
        String paymentType = normalizePaymentType(request.paymentType());

        PaymentReadyInfo info = paymentMapper.findPaymentReadyInfoForUpdate(
                request.reservationId()
        );

        if (info == null) {
            throw new ReservationNotFoundException("결제 가능한 예약 정보를 찾을 수 없습니다.");
        }

        validatePaymentOwner(userId, info.getUserId());
        validateReservationStatus(paymentType, info);

        Payment existingPayment = paymentMapper.findByReservationIdAndPaymentType(
                info.getReservationId(),
                paymentType
        );

        if (existingPayment != null) {
            if ("DONE".equals(existingPayment.getStatus())) {
                throw new PaymentDuplicatedException("이미 완료된 결제입니다.");
            }

            return toReadyResponse(existingPayment, info);
        }

        Long amount = calculatePaymentAmount(paymentType, info);

        if (amount == null || amount <= 0) {
            throw new PaymentAmountInvalidException("결제 가능한 금액을 확인할 수 없습니다.");
        }

        Payment payment = new Payment();
        payment.setReservationId(info.getReservationId());
        payment.setUserId(info.getUserId());
        payment.setOrderId(createOrderId(info.getReservationId()));
        payment.setOrderName(createOrderName(paymentType, info.getReservationId()));
        payment.setPaymentType(paymentType);
        payment.setStatus("READY");
        payment.setTotalAmount(amount);
        payment.setBalanceAmount(amount);
        payment.setPlatformFee(0L);
        payment.setRequestedAt(nowString());

        int insertedCount = paymentMapper.insertReadyPayment(payment);

        if (insertedCount != 1) {
            throw new DatabaseOperationFailedException("결제 준비 데이터 저장에 실패했습니다.");
        }

        return toReadyResponse(payment, info);
    }

    @Transactional
    public PaymentConfirmResponse mockConfirm(Long userId, PaymentMockConfirmRequest request) {
        Payment payment = paymentMapper.findPaymentByOrderIdForUpdate(request.orderId());

        if (payment == null) {
            throw new PaymentNotFoundException("결제 정보를 찾을 수 없습니다.");
        }

        validatePaymentOwner(userId, payment.getUserId());

        if ("DONE".equals(payment.getStatus())) {
            return toConfirmResponse(payment);
        }

        if (!"READY".equals(payment.getStatus())) {
            throw new PaymentInvalidStatusException("결제 준비 상태가 아닌 결제입니다.");
        }

        if (!Objects.equals(payment.getTotalAmount(), request.amount())) {
            throw new PaymentAmountMismatchException("결제 금액이 일치하지 않습니다.");
        }

        PaymentReadyInfo info = findCurrentReadyInfo(payment);
        validateReservationStatus(payment.getPaymentType(), info);

        String now = nowString();

        payment.setPaymentKey("mock_payment_key_" + payment.getPaymentId());
        payment.setMethod("MOCK");
        payment.setStatus("DONE");
        payment.setBalanceAmount(payment.getTotalAmount());
        payment.setLastTransactionKey("mock_transaction_" + payment.getPaymentId());
        payment.setIsPartialCancelable(true);

        if (payment.getRequestedAt() == null || payment.getRequestedAt().isBlank()) {
            payment.setRequestedAt(now);
        }

        payment.setApprovedAt(now);
        payment.setReceiptUrl("https://mock.tosspayments.com/receipt/" + payment.getOrderId());
        payment.setRawResponse("""
                {"mock":true,"status":"DONE"}
                """);

        int updatedPaymentCount = paymentMapper.updatePaymentDone(payment);

        if (updatedPaymentCount != 1) {
            throw new DatabaseOperationFailedException("결제 완료 처리에 실패했습니다.");
        }

        String nextReservationStatus = getNextReservationStatus(payment.getPaymentType());
        String currentReservationStatus = getCurrentReservationStatus(payment.getPaymentType());

        int updatedReservationCount = paymentMapper.updateReservationStatus(
                payment.getReservationId(),
                currentReservationStatus,
                nextReservationStatus
        );

        if (updatedReservationCount != 1) {
            throw new DatabaseOperationFailedException("예약 상태 변경에 실패했습니다.");
        }

        return toConfirmResponse(payment);
    }

    @Transactional
    public PaymentConfirmResponse confirmTossPayment(Long userId, PaymentConfirmRequest request) {

        // 1. orderId로 우리 DB에 저장된 결제 정보 조회
        Payment payment = paymentMapper.findPaymentByOrderIdForUpdate(request.orderId());

        if (payment == null) {
            throw new PaymentNotFoundException("결제 정보를 찾을 수 없습니다.");
        }

        validatePaymentOwner(userId, payment.getUserId());

        // 2. 이미 결제 완료된 건이면 Toss에 다시 승인 요청하지 않음
        if ("DONE".equals(payment.getStatus())) {
            if (payment.getPaymentKey() != null
                    && payment.getPaymentKey().equals(request.paymentKey())) {
                return PaymentConfirmResponse.from(payment);
            }

            throw new PaymentFailedException("이미 다른 결제키로 승인된 주문입니다.");
        }

        // 3. 프론트에서 돌아온 amount와 DB에 저장된 결제 예정 금액 비교
        if (!payment.getTotalAmount().equals(request.amount())) {
            throw new PaymentAmountMismatchException("결제 금액이 일치하지 않습니다.");
        }

        PaymentReadyInfo info = findCurrentReadyInfo(payment);
        validateReservationStatus(payment.getPaymentType(), info);

        // 4. Toss Payments 실제 결제 승인 API 호출
        TossConfirmResponse tossResponse = tossPaymentClient.confirm(request);

        // 5. Toss 응답 상태 확인
        if (!"DONE".equals(tossResponse.status())) {
            throw new PaymentFailedException("결제가 정상 승인되지 않았습니다.");
        }

        // 6. 영수증 URL 꺼내기
        String receiptUrl = null;

        if (tossResponse.receipt() != null) {
            receiptUrl = tossResponse.receipt().url();
        }

        // 7. Toss 승인 결과를 payment 테이블에 반영
        int updatedPaymentCount = paymentMapper.updateTossSuccess(
                payment.getPaymentId(),
                tossResponse.paymentKey(),
                tossResponse.method(),
                tossResponse.status(),
                tossResponse.totalAmount(),
                tossResponse.balanceAmount(),
                receiptUrl,
                tossResponse.approvedAt()
        );

        if (updatedPaymentCount != 1) {
            throw new PaymentFailedException("결제 승인 정보 저장에 실패했습니다.");
        }

        // 8. 예약 상태를 결제 타입에 맞게 변경
        String nextReservationStatus = getNextReservationStatus(payment.getPaymentType());
        String currentReservationStatus = getCurrentReservationStatus(payment.getPaymentType());

        int updatedReservationCount = paymentMapper.updateReservationStatus(
                payment.getReservationId(),
                currentReservationStatus,
                nextReservationStatus
        );

        if (updatedReservationCount != 1) {
            throw new PaymentFailedException("예약 상태 변경에 실패했습니다.");
        }

        // 9. 갱신된 결제 정보 다시 조회 후 응답 반환
        Payment updatedPayment = paymentMapper.findById(payment.getPaymentId())
                .orElseThrow(() -> new PaymentNotFoundException("결제 정보를 찾을 수 없습니다."));

        return PaymentConfirmResponse.from(updatedPayment);
    }

    private String normalizePaymentType(String paymentType) {
        if (paymentType == null || paymentType.isBlank()) {
            throw new PaymentTypeInvalidException("결제 타입이 필요합니다.");
        }

        return switch (paymentType) {
            case "DEPOSIT", "BALANCE" -> paymentType;
            default -> throw new PaymentTypeInvalidException("지원하지 않는 결제 타입입니다.");
        };
    }

    private void validateReservationStatus(String paymentType, PaymentReadyInfo info) {
        String reservationStatus = info.getReservationStatus();

        switch (paymentType) {
            case "DEPOSIT" -> {
                if (!"ESTIMATED".equals(reservationStatus)) {
                    throw new ReservationInvalidStatusException("견적이 도착한 이후에 결제를 진행할 수 있습니다.");
                }
            }
            case "BALANCE" -> {
                if (!"CONFIRMED".equals(reservationStatus)) {
                    throw new ReservationInvalidStatusException("계약금 결제 완료 이후에 잔금 결제를 진행할 수 있습니다.");
                }
                validateBalancePaymentDeadline(info);
            }
            default -> throw new PaymentTypeInvalidException("지원하지 않는 결제 타입입니다.");
        }
    }

    private Long calculatePaymentAmount(String paymentType, PaymentReadyInfo info) {
        Long payableAmount = info.getPayableAmount();

        if (payableAmount == null || payableAmount <= 0) {
            throw new PaymentAmountInvalidException("결제 기준 금액이 올바르지 않습니다.");
        }

        return switch (paymentType) {
            case "DEPOSIT" -> payableAmount / 10;
            case "BALANCE" -> calculateBalanceAmount(info.getReservationId(), payableAmount);
            default -> throw new PaymentTypeInvalidException("지원하지 않는 결제 타입입니다.");
        };
    }

    private Long calculateBalanceAmount(Long reservationId, Long payableAmount) {
        Long completedDepositAmount = paymentMapper.findCompletedDepositAmount(reservationId);

        if (completedDepositAmount == null || completedDepositAmount <= 0) {
            throw new PaymentFailedException("계약금 결제 완료 내역이 없어 잔금 결제를 진행할 수 없습니다.");
        }

        Long balanceAmount = payableAmount - completedDepositAmount;

        if (balanceAmount <= 0) {
            throw new PaymentAmountInvalidException("잔금 결제 금액이 올바르지 않습니다.");
        }

        return balanceAmount;
    }

    private String createOrderId(Long reservationId) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return "SDR_" + reservationId + "_" + uuid;
    }

    private String createOrderName(String paymentType, Long reservationId) {
        return switch (paymentType) {
            case "DEPOSIT" -> "상다리 계약금 결제 #" + reservationId;
            case "BALANCE" -> "상다리 잔금 결제 #" + reservationId;
            default -> "상다리 결제 #" + reservationId;
        };
    }

    private String getNextReservationStatus(String paymentType) {
        return switch (paymentType) {
            case "DEPOSIT" -> "CONFIRMED";
            case "BALANCE" -> "PAYMENT_COMPLETED";
            default -> throw new PaymentTypeInvalidException("지원하지 않는 결제 타입입니다.");
        };
    }

    private String getCurrentReservationStatus(String paymentType) {
        return switch (paymentType) {
            case "DEPOSIT" -> "ESTIMATED";
            case "BALANCE" -> "CONFIRMED";
            default -> throw new PaymentTypeInvalidException("지원하지 않는 결제 타입입니다.");
        };
    }

    private void validatePaymentOwner(Long loginUserId, Long ownerUserId) {
        if (loginUserId == null) {
            throw new AuthLoginRequiredException();
        }

        if (!Objects.equals(loginUserId, ownerUserId)) {
            throw new ReservationOwnerMismatchException("본인의 예약 결제만 진행할 수 있습니다.");
        }
    }

    private PaymentReadyInfo findCurrentReadyInfo(Payment payment) {
        PaymentReadyInfo info = paymentMapper.findPaymentReadyInfoForUpdate(payment.getReservationId());

        if (info == null) {
            throw new ReservationNotFoundException("결제 가능한 예약 정보를 찾을 수 없습니다.");
        }

        return info;
    }

    private void validateBalancePaymentDeadline(PaymentReadyInfo info) {
        LocalDate eventDate = parseEventDate(info.getEventStartDate());
        LocalDate paymentDeadline = minusBusinessDays(eventDate, 5);
        LocalDate today = LocalDate.now(KOREA_ZONE_ID);

        if (today.isAfter(paymentDeadline)) {
            throw new ReservationInvalidStatusException("잔금 결제 마감일이 지나 결제를 진행할 수 없습니다.");
        }
    }

    private LocalDate parseEventDate(String eventStartDate) {
        if (eventStartDate == null || eventStartDate.isBlank()) {
            throw new ReservationPaymentInfoNotFoundException("행사 일정 정보를 찾을 수 없습니다.");
        }

        try {
            return LocalDateTime.parse(eventStartDate, DATE_TIME_FORMATTER).toLocalDate();
        } catch (DateTimeParseException ignored) {
            if (eventStartDate.length() < 10) {
                throw new ReservationPaymentInfoNotFoundException("행사 일정 정보가 올바르지 않습니다.");
            }

            return LocalDate.parse(eventStartDate.substring(0, 10));
        }
    }

    private LocalDate minusBusinessDays(LocalDate date, int businessDays) {
        LocalDate cursor = date;
        int remainingDays = businessDays;

        while (remainingDays > 0) {
            cursor = cursor.minusDays(1);

            if (isBusinessDay(cursor)) {
                remainingDays--;
            }
        }

        return cursor;
    }

    private boolean isBusinessDay(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }

    private PaymentReadyResponse toReadyResponse(Payment payment, PaymentReadyInfo info) {
        List<PaymentReadyMenuResponse> menus = paymentMapper.findMenusByReservationId(info.getReservationId());

        return new PaymentReadyResponse(
                payment.getPaymentId(),
                payment.getOrderId(),
                payment.getOrderName(),
                payment.getTotalAmount(),
                payment.getPaymentType(),
                info.getCustomerName(),
                info.getCustomerEmail(),
                info.getReservationId(),
                info.getStoreName(),
                info.getEventStartDate(),
                info.getEventEndDate(),
                formatEventLocation(info),
                info.getQuotedPrice(),
                info.getPayableAmount(),
                menus
        );
    }

    private String formatEventLocation(PaymentReadyInfo info) {
        if (info.getAddrBase() == null || info.getAddrBase().isBlank()) {
            return info.getAddrDetail();
        }

        if (info.getAddrDetail() == null || info.getAddrDetail().isBlank()) {
            return info.getAddrBase();
        }

        return info.getAddrBase() + " " + info.getAddrDetail();
    }

    private String nowString() {
        return LocalDateTime.now()
                .format(DATE_TIME_FORMATTER);
    }

    private PaymentConfirmResponse toConfirmResponse(Payment payment) {
        return new PaymentConfirmResponse(
                payment.getPaymentId(),
                payment.getReservationId(),
                payment.getOrderId(),
                payment.getPaymentKey(),
                payment.getPaymentType(),
                payment.getMethod(),
                payment.getStatus(),
                payment.getTotalAmount(),
                payment.getBalanceAmount(),
                payment.getReceiptUrl(),
                payment.getApprovedAt()
        );
    }
}
