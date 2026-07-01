package com.sangdari.domain.reservation.mapper;

import com.sangdari.domain.reservation.responses.ReservationMyItemResponse;
import com.sangdari.domain.reservation.responses.ReservationMyMenuResponse;
import com.sangdari.domain.reservation.type.ReservationStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReservationQueryMapper {
    long countMyReservations(
            @Param("userId") Long userId,
            @Param("status") ReservationStatus status
    );

    List<ReservationMyItemResponse> findMyReservations(
            @Param("userId") Long userId,
            @Param("status") ReservationStatus status,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    List<ReservationMyMenuResponse> findMenusByReservationIds(@Param("reservationIds") List<Long> reservationIds);
}
