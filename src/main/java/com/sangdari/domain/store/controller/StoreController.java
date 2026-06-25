package com.sangdari.domain.store.controller;

import com.sangdari.domain.store.requests.StoreListReq;
import com.sangdari.domain.store.responses.StoreListRes;
import com.sangdari.domain.store.services.StoreService;
import com.sangdari.global.responses.GlobalRes;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class StoreController {
//    private final

    // 필터링한 업체 목록 데이터
    @GetMapping("/stores")
    public ResponseEntity<GlobalRes<StoreListRes>> storeList(StoreListReq storeListReq) {

//        StoreListRes storeListRes = StoreService.class

        return  ResponseEntity.status(200).body(
                GlobalRes.<StoreListRes>builder()
                        .code("00")
                        .message("정상처리")
                        .build()
        );
    }
}
