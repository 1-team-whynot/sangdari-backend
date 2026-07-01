package com.sangdari.domain.reservation.services;

import com.sangdari.domain.reservation.entities.Reservation;
import com.sangdari.domain.reservation.entities.ReservationCancelInfo;
import com.sangdari.domain.reservation.entities.ReservationMenu;
import com.sangdari.domain.reservation.entities.ReservationStore;
import com.sangdari.domain.reservation.mapper.ReservationCreateMapper;
import com.sangdari.domain.reservation.mapper.ReservationMenuMapper;
import com.sangdari.domain.reservation.mapper.ReservationQueryMapper;
import com.sangdari.domain.reservation.mapper.ReservationStatusMapper;
import com.sangdari.domain.reservation.requests.ReservationCreateRequest;
import com.sangdari.domain.reservation.requests.ReservationMyListRequest;
import com.sangdari.domain.reservation.responses.ReservationCancelResponse;
import com.sangdari.domain.reservation.responses.ReservationCreateResponse;
import com.sangdari.domain.reservation.responses.ReservationMenuResponse;
import com.sangdari.domain.reservation.responses.ReservationMyItemResponse;
import com.sangdari.domain.reservation.responses.ReservationMyListResponse;
import com.sangdari.domain.reservation.responses.ReservationMyMenuResponse;
import com.sangdari.domain.reservation.type.ReservationStatus;
import com.sangdari.domain.user.entities.User;
import com.sangdari.domain.user.mapper.UserMapper;
import com.sangdari.global.exception.custom.AuthLoginRequiredException;
import com.sangdari.global.exception.custom.ChecklistDateTooSoonException;
import com.sangdari.global.exception.custom.ReservationAlreadyCanceledException;
import com.sangdari.global.exception.custom.ReservationAlreadyCompletedException;
import com.sangdari.global.exception.custom.ReservationInvalidStatusException;
import com.sangdari.global.exception.custom.ReservationNotFoundException;
import com.sangdari.global.exception.custom.ReservationOwnerMismatchException;
import com.sangdari.global.exception.custom.ReservationRequiredMissingException;
import com.sangdari.global.exception.custom.StoreAlreadyReservedException;
import com.sangdari.global.exception.custom.StoreMenuInvalidException;
import com.sangdari.global.exception.custom.StoreMenuRequiredException;
import com.sangdari.global.exception.custom.StoreNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private static final String MY_RESERVATIONS_SUCCESS_MESSAGE = "내 요청 목록 조회가 완료되었습니다.";
    private static final String MY_RESERVATIONS_EMPTY_MESSAGE = "조회된 요청 내역이 없습니다.";
    private static final String RESERVATION_CANCEL_SUCCESS_MESSAGE = "예약이 취소되었습니다.";
    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Set<ReservationStatus> CUSTOMER_CANCELABLE_STATUSES = EnumSet.of(
            ReservationStatus.REQUESTED,
            ReservationStatus.ESTIMATED,
            ReservationStatus.CONFIRMED
    );

    private final ReservationCreateMapper reservationCreateMapper;
    private final ReservationMenuMapper reservationMenuMapper;
    private final ReservationQueryMapper reservationQueryMapper;
    private final ReservationStatusMapper reservationStatusMapper;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public ReservationMyListResponse getMyReservations(Long userId, ReservationMyListRequest reservationMyListRequest) {
        if (userId == null) {
            throw new AuthLoginRequiredException();
        }

        User user = userMapper.findByPk(userId);
        if (user == null || user.getWithdrawAt() != null) {
            throw new AuthLoginRequiredException();
        }

        int page = reservationMyListRequest.pageOrDefault();
        int limit = reservationMyListRequest.limitOrDefault();
        int offset = reservationMyListRequest.getOffset();

        ReservationStatus status = reservationMyListRequest.status();

        long totalCount = reservationQueryMapper.countMyReservations(userId, status);
        List<ReservationMyItemResponse> reservations = reservationQueryMapper.findMyReservations(userId, status, limit, offset);

        List<Long> reservationIds = reservations.stream()
                .map(ReservationMyItemResponse::getReservationId)
                .toList();

        Map<Long, List<ReservationMyMenuResponse>> menusByReservationId = reservationIds.isEmpty()
                ? Map.of()
                : reservationQueryMapper.findMenusByReservationIds(reservationIds).stream()
                        .collect(Collectors.groupingBy(ReservationMyMenuResponse::getReservationId));

        reservations.forEach(reservation -> reservation.setMenus(
                menusByReservationId.getOrDefault(reservation.getReservationId(), List.of())
        ));

        return ReservationMyListResponse.builder()
                .reservations(reservations)
                .totalCount(totalCount)
                .page(page)
                .limit(limit)
                .message(reservations.isEmpty() ? MY_RESERVATIONS_EMPTY_MESSAGE : MY_RESERVATIONS_SUCCESS_MESSAGE)
                .build();
    }

    @Transactional
    public ReservationCreateResponse createReservation(Long userId, ReservationCreateRequest reservationCreateRequest) {
        if (userId == null || !reservationCreateMapper.existsActiveUserById(userId)) {
            throw new AuthLoginRequiredException();
        }

        ReservationStore reservationStore = reservationCreateMapper.findStoreForReservation(reservationCreateRequest.storeId());
        if (reservationStore == null) {
            throw new StoreNotFoundException();
        }

        LocalDateTime eventStartDate = parseStartDate(reservationCreateRequest.eventStartDate());
        LocalDateTime eventEndDate = parseEndDate(reservationCreateRequest.eventEndDate());

        if (eventStartDate.toLocalDate().isBefore(LocalDate.now(KOREA_ZONE_ID).plusDays(14))) {
            throw new ChecklistDateTooSoonException();
        }

        if (eventEndDate.isBefore(eventStartDate)) {
            throw new ReservationRequiredMissingException();
        }

        if (reservationCreateMapper.existsReservedStoreInPeriod(
                reservationCreateRequest.storeId(),
                eventStartDate.format(DATE_TIME_FORMATTER),
                eventEndDate.format(DATE_TIME_FORMATTER)
        )) {
            throw new StoreAlreadyReservedException();
        }

        if (reservationCreateRequest.menuIds() == null || reservationCreateRequest.menuIds().isEmpty()) {
            throw new StoreMenuRequiredException();
        }

        List<Long> menuIds = reservationCreateRequest.menuIds().stream()
                .distinct()
                .toList();

        List<ReservationMenuResponse> menus = reservationCreateMapper.findActiveMenusByStoreId(reservationCreateRequest.storeId(), menuIds);
        if (menus.size() != menuIds.size()) {
            throw new StoreMenuInvalidException();
        }

        String requestMemo = reservationCreateRequest.requestMemo() == null || reservationCreateRequest.requestMemo().isBlank()
                ? null
                : reservationCreateRequest.requestMemo().trim();

        Reservation reservation = Reservation.builder()
                .userId(userId)
                .storeId(reservationCreateRequest.storeId())
                .addrBase(reservationCreateRequest.addrBase().trim())
                .addrDetail(reservationCreateRequest.addrDetail().trim())
                .eventStartDate(eventStartDate.format(DATE_TIME_FORMATTER))
                .eventEndDate(eventEndDate.format(DATE_TIME_FORMATTER))
                .headcount(reservationCreateRequest.headcount())
                .powerAvailable(reservationStore.batterySupported())
                .requestMemo(requestMemo)
                .status(ReservationStatus.REQUESTED)
                .build();

        int insertedReservationCount = reservationCreateMapper.insert(reservation);
        if (insertedReservationCount != 1 || reservation.getReservationId() == null) {
            throw new ReservationRequiredMissingException();
        }

        List<ReservationMenu> reservationMenus = menuIds.stream()
                .map(menuId -> ReservationMenu.builder()
                        .reservationId(reservation.getReservationId())
                        .menuId(menuId)
                        .build())
                .toList();

        int insertedMenuCount = reservationMenuMapper.insertAll(reservationMenus);
        if (insertedMenuCount != reservationMenus.size()) {
            throw new ReservationRequiredMissingException();
        }

        return ReservationCreateResponse.builder()
                .reservationId(reservation.getReservationId())
                .status(reservation.getStatus())
                .storeId(reservation.getStoreId())
                .imageUrl(reservationStore.imageUrl())
                .businessName(reservationStore.businessName())
                .businessNumber(reservationStore.businessNumber())
                .addrBase(reservation.getAddrBase())
                .addrDetail(reservation.getAddrDetail())
                .eventStartDate(reservation.getEventStartDate())
                .eventEndDate(reservation.getEventEndDate())
                .headcount(reservation.getHeadcount())
                .powerAvailable(reservation.getPowerAvailable())
                .requestMemo(reservation.getRequestMemo())
                .menus(menus)
                .build();
    }

    @Transactional
    public ReservationCancelResponse cancelReservation(Long userId, Long reservationId) {
        if (userId == null) {
            throw new AuthLoginRequiredException();
        }

        if (reservationId == null) {
            throw new ReservationRequiredMissingException();
        }

        ReservationCancelInfo reservation = reservationStatusMapper.findForCancelUpdate(reservationId);
        if (reservation == null) {
            throw new ReservationNotFoundException("예약 정보를 찾을 수 없습니다.");
        }

        if (!Objects.equals(userId, reservation.getUserId())) {
            throw new ReservationOwnerMismatchException("본인의 예약만 취소할 수 있습니다.");
        }

        validateCancelableStatus(reservation.getStatus());
        validateCancelableDeadline(reservation.getEventStartDate());

        Long depositAmount = reservationStatusMapper.findDoneDepositAmount(reservationId);
        int refundRate = calculateRefundRate(parseEventDate(reservation.getEventStartDate()));
        long expectedRefundAmount = calculateExpectedRefundAmount(depositAmount, refundRate);

        int updatedCount = reservationStatusMapper.updateStatusToCanceled(
                reservationId,
                reservation.getStatus()
        );

        if (updatedCount != 1) {
            throw new ReservationInvalidStatusException("예약 취소 처리에 실패했습니다.");
        }

        return new ReservationCancelResponse(
                reservationId,
                ReservationStatus.CANCELED,
                depositAmount == null ? 0L : depositAmount,
                refundRate,
                expectedRefundAmount,
                RESERVATION_CANCEL_SUCCESS_MESSAGE
        );
    }

    @Scheduled(cron = "0 10 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void cancelOverdueBalanceReservations() {
        List<ReservationCancelInfo> candidates = reservationStatusMapper.findConfirmedWithoutDoneBalance();

        for (ReservationCancelInfo candidate : candidates) {
            if (isAfterPaymentDeadline(candidate.getEventStartDate())) {
                reservationStatusMapper.updateStatusToCanceled(
                        candidate.getReservationId(),
                        ReservationStatus.CONFIRMED
                );
            }
        }
    }

    private LocalDateTime parseStartDate(String date) {
        return parseDate(date, LocalTime.MIN);
    }

    private LocalDateTime parseEndDate(String date) {
        return parseDate(date, LocalTime.MAX.withNano(0));
    }

    private LocalDateTime parseDate(String date, LocalTime defaultTime) {
        try {
            return LocalDate.parse(date.trim(), DATE_FORMATTER).atTime(defaultTime);
        } catch (DateTimeParseException ignored) {
            try {
                return LocalDateTime.parse(date.trim(), DATE_TIME_FORMATTER);
            } catch (DateTimeParseException exception) {
                throw new ReservationRequiredMissingException();
            }
        }
    }

    private void validateCancelableStatus(ReservationStatus status) {
        if (ReservationStatus.CANCELED.equals(status)) {
            throw new ReservationAlreadyCanceledException("이미 취소된 예약입니다.");
        }

        if (ReservationStatus.PAYMENT_COMPLETED.equals(status) || ReservationStatus.COMPLETED.equals(status)) {
            throw new ReservationAlreadyCompletedException("결제 완료 또는 완료 상태의 예약은 취소할 수 없습니다.");
        }

        if (!CUSTOMER_CANCELABLE_STATUSES.contains(status)) {
            throw new ReservationInvalidStatusException("현재 상태에서는 예약을 취소할 수 없습니다.");
        }
    }

    private void validateCancelableDeadline(String eventStartDate) {
        if (isAfterPaymentDeadline(eventStartDate)) {
            throw new ReservationInvalidStatusException("행사일 기준 5영업일 전이 지나 예약을 취소할 수 없습니다.");
        }
    }

    private boolean isAfterPaymentDeadline(String eventStartDate) {
        LocalDate eventDate = parseEventDate(eventStartDate);
        LocalDate paymentDeadline = minusBusinessDays(eventDate, 5);
        LocalDate today = LocalDate.now(KOREA_ZONE_ID);

        return today.isAfter(paymentDeadline);
    }

    private LocalDate parseEventDate(String eventStartDate) {
        if (eventStartDate == null || eventStartDate.isBlank()) {
            throw new ReservationRequiredMissingException();
        }

        try {
            return LocalDateTime.parse(eventStartDate, DATE_TIME_FORMATTER).toLocalDate();
        } catch (DateTimeParseException ignored) {
            if (eventStartDate.length() < 10) {
                throw new ReservationRequiredMissingException();
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

    private int calculateRefundRate(LocalDate eventDate) {
        long daysUntilEvent = ChronoUnit.DAYS.between(LocalDate.now(KOREA_ZONE_ID), eventDate);

        if (daysUntilEvent >= 30) {
            return 100;
        }

        if (daysUntilEvent >= 14) {
            return 50;
        }

        if (daysUntilEvent >= 7) {
            return 20;
        }

        return 0;
    }

    private long calculateExpectedRefundAmount(Long depositAmount, int refundRate) {
        if (depositAmount == null || depositAmount <= 0 || refundRate <= 0) {
            return 0L;
        }

        return depositAmount * refundRate / 100;
    }
}
