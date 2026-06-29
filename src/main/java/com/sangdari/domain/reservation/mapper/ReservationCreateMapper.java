package com.sangdari.domain.reservation.mapper;

import com.sangdari.domain.reservation.entities.Reservation;
import com.sangdari.domain.reservation.entities.ReservationStore;
import com.sangdari.domain.reservation.responses.ReservationMenuResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReservationCreateMapper {
    boolean existsActiveUserById(@Param("userId") Long userId);

    ReservationStore findStoreForReservation(@Param("storeId") Long storeId);

    boolean existsReservedStoreInPeriod(
            @Param("storeId") Long storeId,
            @Param("eventStartDate") String eventStartDate,
            @Param("eventEndDate") String eventEndDate
    );

    List<ReservationMenuResponse> findActiveMenusByStoreId(
            @Param("storeId") Long storeId,
            @Param("menuIds") List<Long> menuIds
    );

    int insert(Reservation reservation);
}
