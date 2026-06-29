package com.sangdari.domain.reservation.entities;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReservationMenu {
    private Long reservationId;
    private Long menuId;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;
}
