package com.sangdari.domain.payment.mappers;

import com.sangdari.domain.payment.entities.Payment;
import com.sangdari.domain.payment.entities.PaymentReadyInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface PaymentMapper {
    // 결제 준비 정보 조회
    PaymentReadyInfo findPaymentReadyInfoForUpdate(
            @Param("reservationId") Long reservationId
    );
    // 완료된 결제 존재 여부 확인
    boolean existsDonePayment(
            @Param("reservationId") Long reservationId,
            @Param("paymentType") String paymentType
    );
    // READY 결제 저장
    int insertReadyPayment(Payment payment);

    // orderId로 결제 조회
    Payment findPaymentByOrderIdForUpdate(@Param("orderId") String orderId);

    // 결제 성공 처리
    int updatePaymentDone(Payment payment);

    // 결제 실패 처리
    int updatePaymentFailed(Payment payment);

    // 예약 상태 변경
    int updateReservationStatus(
            @Param("reservationId") Long reservationId,
            @Param("status") String status
    );
    // 완료된 계약금 금액 조회
    Long findCompletedDepositAmount(@Param("reservationId") Long reservationId);

    Optional<Payment> findByOrderId(String orderId);

    Optional<Payment> findById(Long paymentId);

    int updateTossSuccess(
            Long paymentId,
            String paymentKey,
            String paymentType,
            String method,
            Long totalAmount,
            Long balanceAmount,
            String receiptUrl,
            String approvedAt
    );
}
