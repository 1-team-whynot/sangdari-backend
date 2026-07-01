package com.sangdari.domain.reservation.responses;

import com.sangdari.domain.reservation.type.ReservationStatus;

public record ReservationCancelResponse(
        Long reservationId,
        ReservationStatus status,
        Long depositAmount,
        Integer refundRate,
        Long expectedRefundAmount,
        String message
) {
}
