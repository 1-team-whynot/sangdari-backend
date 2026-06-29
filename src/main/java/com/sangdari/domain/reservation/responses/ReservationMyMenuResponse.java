package com.sangdari.domain.reservation.responses;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationMyMenuResponse {
    @JsonIgnore
    private Long reservationId;
    private Long menuId;
    private String name;
    private String description;
    private Integer price;
    private String imageUrl;
}
