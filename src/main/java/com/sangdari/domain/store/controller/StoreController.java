package com.sangdari.domain.store.controller;

import com.sangdari.domain.store.requests.AllStoreListReq;
import com.sangdari.domain.store.requests.StoreListReq;
import com.sangdari.domain.store.responses.StoreListRes;
import com.sangdari.domain.store.services.StoreService;
import com.sangdari.global.responses.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class StoreController {
    private final StoreService storeService;

    // 전체 업체 목록 데이터
    @GetMapping("/allStores")
    public ResponseEntity<GlobalResponse<StoreListRes>> allStoreList(AllStoreListReq allStoreListReq) {

        StoreListRes storeListRes = storeService.allStoreList(allStoreListReq);

        return  ResponseEntity.status(200).body(
                GlobalResponse.<StoreListRes>builder()
                        .code("00")
                        .message("정상처리")
                        .data(storeListRes)
                        .build()
        );
    }

    // 필터링한 업체 목록 데이터
    @GetMapping("/stores")
    public ResponseEntity<GlobalResponse<StoreListRes>> storeList(StoreListReq storeListReq) {

       StoreListRes storeListRes = storeService.storeList(storeListReq);

        return  ResponseEntity.status(200).body(
                GlobalResponse.<StoreListRes>builder()
                        .code("00")
                        .message("정상처리")
                        .data(storeListRes)
                        .build()
        );
    }
}
