package com.sangdari.domain.payment.mappers;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReservationMapper {
    int updatePaymentDone(@Param("reservationId") Long reservationId);
}
