package com.sangdari.domain.reservation.entities;

import com.sangdari.domain.reservation.type.ReservationStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Reservation {
    private Long reservationId;
    private Long userId;
    private Long storeId;
    private String addrBase;
    private String addrDetail;
    private String eventStartDate;
    private String eventEndDate;
    private Integer headcount;
    private Boolean powerAvailable;
    private String requestMemo;
    private Integer quotedPrice;
    private Integer discountPrice;
    private ReservationStatus status;
    private String createdAt;
    private String updatedAt;
    private String deletedAt;
}
