package com.sangdari.domain.reservation.controller;

import com.sangdari.domain.reservation.requests.ReservationCreateRequest;
import com.sangdari.domain.reservation.requests.ReservationMyListRequest;
import com.sangdari.domain.reservation.responses.ReservationCreateResponse;
import com.sangdari.domain.reservation.responses.ReservationMyListResponse;
import com.sangdari.domain.reservation.services.ReservationService;
import com.sangdari.global.exception.custom.AuthLoginRequiredException;
import com.sangdari.global.responses.GlobalResponse;
import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    @GetMapping("/my")
    public ResponseEntity<GlobalResponse<ReservationMyListResponse>> getMyReservations(
            Authentication authentication,
            @Valid @ModelAttribute ReservationMyListRequest reservationMyListRequest
    ) {
        Long userId = getLoginUserId(authentication);
        ReservationMyListResponse reservationMyListResponse = reservationService.getMyReservations(userId, reservationMyListRequest);

        return ResponseEntity.status(200).body(
                GlobalResponse.<ReservationMyListResponse>builder()
                        .code("00")
                        .message("정상처리")
                        .data(reservationMyListResponse)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<GlobalResponse<ReservationCreateResponse>> createReservation(
            Authentication authentication,
            @Valid @RequestBody ReservationCreateRequest reservationCreateRequest
    ) {
        Long userId = getLoginUserId(authentication);
        ReservationCreateResponse reservationCreateResponse = reservationService.createReservation(userId, reservationCreateRequest);

        return ResponseEntity.status(200).body(
                GlobalResponse.<ReservationCreateResponse>builder()
                        .code("00")
                        .message("정상처리")
                        .data(reservationCreateResponse)
                        .build()
        );
    }

    private Long getLoginUserId(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Claims claims)) {
            throw new AuthLoginRequiredException();
        }

        return Long.parseLong(claims.getSubject());
    }
}
