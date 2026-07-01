package com.sangdari.domain.payment.mappers;

import com.sangdari.domain.payment.entities.Payment;
import com.sangdari.domain.payment.entities.PaymentReadyInfo;
import com.sangdari.domain.payment.responses.PaymentReadyMenuResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PaymentMapper {

    // 결제 준비 정보 조회
    PaymentReadyInfo findPaymentReadyInfoForUpdate(
            @Param("reservationId") Long reservationId
    );

    List<PaymentReadyMenuResponse> findMenusByReservationId(
            @Param("reservationId") Long reservationId
    );

    // 기존 결제 정보 조회
    Payment findByReservationIdAndPaymentType(
            @Param("reservationId") Long reservationId,
            @Param("paymentType") String paymentType
    );

    // 완료된 결제 존재 여부 확인
    boolean existsDonePayment(
            @Param("reservationId") Long reservationId,
            @Param("paymentType") String paymentType
    );

    // READY 결제 저장
    int insertReadyPayment(Payment payment);

    // orderId로 결제 조회
    Payment findPaymentByOrderIdForUpdate(
            @Param("orderId") String orderId
    );

    // 결제 성공 처리
    int updatePaymentDone(Payment payment);

    // 결제 실패 처리
    int updatePaymentFailed(Payment payment);

    // 예약 상태 변경
    int updateReservationStatus(
            @Param("reservationId") Long reservationId,
            @Param("currentStatus") String currentStatus,
            @Param("status") String status
    );

    // 완료된 계약금 금액 조회
    Long findCompletedDepositAmount(
            @Param("reservationId") Long reservationId
    );

    // orderId로 결제 조회
    Optional<Payment> findByOrderId(
            @Param("orderId") String orderId
    );

    // paymentId로 결제 조회
    Optional<Payment> findById(
            @Param("paymentId") Long paymentId
    );

    // Toss 결제 성공 처리
    int updateTossSuccess(
            @Param("paymentId") Long paymentId,
            @Param("paymentKey") String paymentKey,
            @Param("method") String method,
            @Param("status") String status,
            @Param("totalAmount") Long totalAmount,
            @Param("balanceAmount") Long balanceAmount,
            @Param("receiptUrl") String receiptUrl,
            @Param("approvedAt") String approvedAt
    );
}
