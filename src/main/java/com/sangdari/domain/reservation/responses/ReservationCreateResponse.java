package com.sangdari.domain.reservation.responses;

import com.sangdari.domain.reservation.type.ReservationStatus;
import lombok.Builder;

import java.util.List;

@Builder
public record ReservationCreateResponse(
        Long reservationId,
        ReservationStatus status,
        Long storeId,
        String imageUrl,
        String businessName,
        String businessNumber,
        String addrBase,
        String addrDetail,
        String eventStartDate,
        String eventEndDate,
        Integer headcount,
        Boolean powerAvailable,
        String requestMemo,
        List<ReservationMenuResponse> menus
) {
}
