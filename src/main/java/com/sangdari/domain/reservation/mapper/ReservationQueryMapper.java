package com.sangdari.domain.reservation.mapper;

import com.sangdari.domain.reservation.responses.ReservationMyItemResponse;
import com.sangdari.domain.reservation.responses.ReservationMyMenuResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReservationQueryMapper {
    long countMyReservations(@Param("userId") Long userId);

    List<ReservationMyItemResponse> findMyReservations(
            @Param("userId") Long userId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    List<ReservationMyMenuResponse> findMenusByReservationIds(@Param("reservationIds") List<Long> reservationIds);
}
