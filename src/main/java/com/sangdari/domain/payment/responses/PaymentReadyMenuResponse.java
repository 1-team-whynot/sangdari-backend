package com.sangdari.domain.payment.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReadyMenuResponse {
    private Long menuId;
    private String name;
    private String description;
    private Integer price;
    private String imageUrl;
}
