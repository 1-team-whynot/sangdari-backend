package com.sangdari.domain.store.services;

import com.sangdari.domain.store.mapper.StoreMapper;
import com.sangdari.domain.store.requests.StoreListReq;
import com.sangdari.domain.store.responses.StoreItems;
import com.sangdari.domain.store.responses.StoreListRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreService {
   private final StoreMapper storeMapper;

   public StoreListRes storeList(StoreListReq storeListReq) {

      // 현재 페이지의 "전체 업체 목록"들의 데이터 리스트
      List<StoreItems> allStores;
      // -------------------------------------------------------------------------------

      // 현재 페이지의 "필터링된 업체"들의 데이터 리스트
      List<StoreItems> filterStores = storeMapper.getFilteredStores(storeListReq);

      // 불러온 업체 개수, 마지막 페이지 여부
      long totalItems = storeMapper.countStores(storeListReq);
      boolean isLastPage = (storeListReq.getOffset() + filterStores.size()) >= totalItems;

      // 리턴
      return StoreListRes.builder()
              .stores(filterStores)
              .totalItems(totalItems)
              .isLastPage(isLastPage)
              .build();
   }
}
