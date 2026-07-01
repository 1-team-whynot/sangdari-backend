package com.sangdari.domain.reservation.mapper;

import com.sangdari.domain.reservation.entities.ReservationMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReservationMenuMapper {
    int insertAll(List<ReservationMenu> reservationMenus);
}
