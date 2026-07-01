package com.sangdari.domain.reservation.entities;

import lombok.Builder;

@Builder
public record ReservationStore(
        Long storeId,
        String imageUrl,
        String businessName,
        String businessNumber,
        Boolean batterySupported
) {
}
