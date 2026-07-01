package com.sangdari.domain.reservation.mapper;

import com.sangdari.domain.reservation.entities.ReservationCancelInfo;
import com.sangdari.domain.reservation.type.ReservationStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ReservationStatusMapper {
    ReservationCancelInfo findForCancelUpdate(@Param("reservationId") Long reservationId);

    Long findDoneDepositAmount(@Param("reservationId") Long reservationId);

    int updateStatusToCanceled(
            @Param("reservationId") Long reservationId,
            @Param("currentStatus") ReservationStatus currentStatus
    );

    List<ReservationCancelInfo> findConfirmedWithoutDoneBalance();
}
