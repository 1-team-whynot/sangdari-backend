package com.sangdari.domain.payment.clients;

import com.sangdari.domain.payment.requests.PaymentConfirmRequest;
import com.sangdari.domain.payment.responses.TossConfirmResponse;
import com.sangdari.global.errors.PaymentFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
@Component
public class TossPaymentClient {

    private final RestClient restClient;
    private final String secretKey;

    public TossPaymentClient(
            @Value("${toss.payments.base-url}") String baseUrl,
            @Value("${toss.payments.secret-key}") String secretKey
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();

        this.secretKey = secretKey;
    }

    public TossConfirmResponse confirm(PaymentConfirmRequest request) {
        try {
            return restClient.post()
                    .uri("/v1/payments/confirm")
                    .header(HttpHeaders.AUTHORIZATION, "Basic " + encodeSecretKey())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header("Idempotency-Key", "confirm-" + request.orderId())
                    .body(request)
                    .retrieve()
                    .body(TossConfirmResponse.class);

        } catch (RestClientResponseException e) {
            log.warn(
                    "Toss 결제 승인 실패 - status={}, response={}",
                    e.getStatusCode(),
                    e.getResponseBodyAsString()
            );

            throw new PaymentFailedException("토스 결제 승인 요청에 실패했습니다.");

        } catch (Exception e) {
            log.error("Toss 결제 승인 요청 중 알 수 없는 오류 발생", e);

            throw new PaymentFailedException("토스 결제 승인 요청 중 서버 오류가 발생했습니다.");
        }
    }

    private String encodeSecretKey() {
        String value = secretKey + ":";

        return Base64.getEncoder()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }


}