package com.sangdari.domain.reservation.entities;

import com.sangdari.domain.reservation.type.ReservationStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReservationCancelInfo {
    private Long reservationId;
    private Long userId;
    private ReservationStatus status;
    private String eventStartDate;
}
