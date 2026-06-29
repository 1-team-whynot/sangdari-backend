package com.sangdari.domain.reservation.services;

import com.sangdari.domain.reservation.entities.Reservation;
import com.sangdari.domain.reservation.entities.ReservationMenu;
import com.sangdari.domain.reservation.entities.ReservationStore;
import com.sangdari.domain.reservation.mapper.ReservationCreateMapper;
import com.sangdari.domain.reservation.mapper.ReservationMenuMapper;
import com.sangdari.domain.reservation.mapper.ReservationQueryMapper;
import com.sangdari.domain.reservation.requests.ReservationCreateRequest;
import com.sangdari.domain.reservation.requests.ReservationMyListRequest;
import com.sangdari.domain.reservation.responses.ReservationCreateResponse;
import com.sangdari.domain.reservation.responses.ReservationMenuResponse;
import com.sangdari.domain.reservation.responses.ReservationMyItemResponse;
import com.sangdari.domain.reservation.responses.ReservationMyListResponse;
import com.sangdari.domain.reservation.responses.ReservationMyMenuResponse;
import com.sangdari.domain.reservation.type.ReservationStatus;
import com.sangdari.domain.user.entities.User;
import com.sangdari.domain.user.mapper.UserMapper;
import com.sangdari.global.exception.custom.AuthLoginRequiredException;
import com.sangdari.global.exception.custom.ChecklistDateTooSoonException;
import com.sangdari.global.exception.custom.ReservationRequiredMissingException;
import com.sangdari.global.exception.custom.StoreAlreadyReservedException;
import com.sangdari.global.exception.custom.StoreMenuInvalidException;
import com.sangdari.global.exception.custom.StoreMenuRequiredException;
import com.sangdari.global.exception.custom.StoreNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private static final String MY_RESERVATIONS_SUCCESS_MESSAGE = "내 요청 목록 조회가 완료되었습니다.";
    private static final String MY_RESERVATIONS_EMPTY_MESSAGE = "조회된 요청 내역이 없습니다.";
    private static final ZoneId KOREA_ZONE_ID = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ReservationCreateMapper reservationCreateMapper;
    private final ReservationMenuMapper reservationMenuMapper;
    private final ReservationQueryMapper reservationQueryMapper;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public ReservationMyListResponse getMyReservations(Long userId, ReservationMyListRequest reservationMyListRequest) {
        if (userId == null) {
            throw new AuthLoginRequiredException();
        }

        User user = userMapper.findByPk(userId);
        if (user == null || user.getWithdrawAt() != null) {
            throw new AuthLoginRequiredException();
        }

        int page = reservationMyListRequest.pageOrDefault();
        int limit = reservationMyListRequest.limitOrDefault();
        int offset = reservationMyListRequest.getOffset();

        long totalCount = reservationQueryMapper.countMyReservations(userId);
        List<ReservationMyItemResponse> reservations = reservationQueryMapper.findMyReservations(userId, limit, offset);

        List<Long> reservationIds = reservations.stream()
                .map(ReservationMyItemResponse::getReservationId)
                .toList();

        Map<Long, List<ReservationMyMenuResponse>> menusByReservationId = reservationIds.isEmpty()
                ? Map.of()
                : reservationQueryMapper.findMenusByReservationIds(reservationIds).stream()
                        .collect(Collectors.groupingBy(ReservationMyMenuResponse::getReservationId));

        reservations.forEach(reservation -> reservation.setMenus(
                menusByReservationId.getOrDefault(reservation.getReservationId(), List.of())
        ));

        return ReservationMyListResponse.builder()
                .reservations(reservations)
                .totalCount(totalCount)
                .page(page)
                .limit(limit)
                .message(reservations.isEmpty() ? MY_RESERVATIONS_EMPTY_MESSAGE : MY_RESERVATIONS_SUCCESS_MESSAGE)
                .build();
    }

    @Transactional
    public ReservationCreateResponse createReservation(Long userId, ReservationCreateRequest reservationCreateRequest) {
        if (userId == null || !reservationCreateMapper.existsActiveUserById(userId)) {
            throw new AuthLoginRequiredException();
        }

        ReservationStore reservationStore = reservationCreateMapper.findStoreForReservation(reservationCreateRequest.storeId());
        if (reservationStore == null) {
            throw new StoreNotFoundException();
        }

        LocalDateTime eventStartDate = parseStartDate(reservationCreateRequest.eventStartDate());
        LocalDateTime eventEndDate = parseEndDate(reservationCreateRequest.eventEndDate());

        if (eventStartDate.toLocalDate().isBefore(LocalDate.now(KOREA_ZONE_ID).plusDays(14))) {
            throw new ChecklistDateTooSoonException();
        }

        if (eventEndDate.isBefore(eventStartDate)) {
            throw new ReservationRequiredMissingException();
        }

        if (reservationCreateMapper.existsReservedStoreInPeriod(
                reservationCreateRequest.storeId(),
                eventStartDate.format(DATE_TIME_FORMATTER),
                eventEndDate.format(DATE_TIME_FORMATTER)
        )) {
            throw new StoreAlreadyReservedException();
        }

        if (reservationCreateRequest.menuIds() == null || reservationCreateRequest.menuIds().isEmpty()) {
            throw new StoreMenuRequiredException();
        }

        List<Long> menuIds = reservationCreateRequest.menuIds().stream()
                .distinct()
                .toList();

        List<ReservationMenuResponse> menus = reservationCreateMapper.findActiveMenusByStoreId(reservationCreateRequest.storeId(), menuIds);
        if (menus.size() != menuIds.size()) {
            throw new StoreMenuInvalidException();
        }

        String requestMemo = reservationCreateRequest.requestMemo() == null || reservationCreateRequest.requestMemo().isBlank()
                ? null
                : reservationCreateRequest.requestMemo().trim();

        Reservation reservation = Reservation.builder()
                .userId(userId)
                .storeId(reservationCreateRequest.storeId())
                .addrBase(reservationCreateRequest.addrBase().trim())
                .addrDetail(reservationCreateRequest.addrDetail().trim())
                .eventStartDate(eventStartDate.format(DATE_TIME_FORMATTER))
                .eventEndDate(eventEndDate.format(DATE_TIME_FORMATTER))
                .headcount(reservationCreateRequest.headcount())
                .powerAvailable(reservationStore.batterySupported())
                .requestMemo(requestMemo)
                .status(ReservationStatus.REQUESTED)
                .build();

        int insertedReservationCount = reservationCreateMapper.insert(reservation);
        if (insertedReservationCount != 1 || reservation.getReservationId() == null) {
            throw new ReservationRequiredMissingException();
        }

        List<ReservationMenu> reservationMenus = menuIds.stream()
                .map(menuId -> ReservationMenu.builder()
                        .reservationId(reservation.getReservationId())
                        .menuId(menuId)
                        .build())
                .toList();

        int insertedMenuCount = reservationMenuMapper.insertAll(reservationMenus);
        if (insertedMenuCount != reservationMenus.size()) {
            throw new ReservationRequiredMissingException();
        }

        return ReservationCreateResponse.builder()
                .reservationId(reservation.getReservationId())
                .status(reservation.getStatus())
                .storeId(reservation.getStoreId())
                .imageUrl(reservationStore.imageUrl())
                .businessName(reservationStore.businessName())
                .businessNumber(reservationStore.businessNumber())
                .addrBase(reservation.getAddrBase())
                .addrDetail(reservation.getAddrDetail())
                .eventStartDate(reservation.getEventStartDate())
                .eventEndDate(reservation.getEventEndDate())
                .headcount(reservation.getHeadcount())
                .powerAvailable(reservation.getPowerAvailable())
                .requestMemo(reservation.getRequestMemo())
                .menus(menus)
                .build();
    }

    private LocalDateTime parseStartDate(String date) {
        return parseDate(date, LocalTime.MIN);
    }

    private LocalDateTime parseEndDate(String date) {
        return parseDate(date, LocalTime.MAX.withNano(0));
    }

    private LocalDateTime parseDate(String date, LocalTime defaultTime) {
        try {
            return LocalDate.parse(date.trim(), DATE_FORMATTER).atTime(defaultTime);
        } catch (DateTimeParseException ignored) {
            try {
                return LocalDateTime.parse(date.trim(), DATE_TIME_FORMATTER);
            } catch (DateTimeParseException exception) {
                throw new ReservationRequiredMissingException();
            }
        }
    }
}
