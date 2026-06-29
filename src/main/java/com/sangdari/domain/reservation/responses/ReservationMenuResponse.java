package com.sangdari.domain.reservation.responses;

import lombok.Builder;

@Builder
public record ReservationMenuResponse(
        Long menuId,
        String name,
        String description,
        Integer price,
        String imageUrl
) {
}
