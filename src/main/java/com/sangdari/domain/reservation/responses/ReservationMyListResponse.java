package com.sangdari.domain.reservation.responses;

import lombok.Builder;

import java.util.List;

@Builder
public record ReservationMyListResponse(
        List<ReservationMyItemResponse> reservations,
        long totalCount,
        int page,
        int limit,
        String message
) {
}
