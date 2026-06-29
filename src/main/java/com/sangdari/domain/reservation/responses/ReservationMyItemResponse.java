package com.sangdari.domain.reservation.responses;

import com.sangdari.domain.reservation.type.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationMyItemResponse {
    private Long reservationId;
    private ReservationStatus status;
    private Long storeId;
    private String businessName;
    private String imageUrl;
    private String addrBase;
    private String addrDetail;
    private String eventStartDate;
    private String eventEndDate;
    private Integer headcount;
    private Boolean powerAvailable;
    private String requestMemo;
    private Integer quotedPrice;
    private Integer discountPrice;
    private List<ReservationMyMenuResponse> menus;
    private String createdAt;
}
