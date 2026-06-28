package com.sangdari.domain.store.services;

import com.sangdari.domain.store.mapper.StoreMapper;
import com.sangdari.domain.store.requests.AllStoreListReq;
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

   public  StoreListRes allStoreList(AllStoreListReq allStoreListReq) {
      // offset
      int offset = allStoreListReq.getOffset();

      // 현재 페이지의 "필터링 안 된 업체 목록"들의 데이터 리스트
      List<StoreItems> allStores = storeMapper.getAllStores(offset, allStoreListReq);

      // 전체 업체 수
      long totalStoreCount = storeMapper.getTotalStoreCount();
      boolean isLastPage = (allStoreListReq.getOffset() + allStores.size()) >= totalStoreCount;

      // 리턴
      return StoreListRes.builder()
              .stores(allStores)
              .totalItems(totalStoreCount)
              .isLastPage(isLastPage)
              .build();
   }

   public StoreListRes storeList(StoreListReq storeListReq) {

      // offset
      int offset = storeListReq.getOffset();

      // 현재 페이지의 "필터링된 업체"들의 데이터 리스트
      List<StoreItems> filterStores = storeMapper.getFilteredStores(offset, storeListReq);

      // 불러온 업체 개수, 마지막 페이지 여부
      long totalItems = storeMapper.countStores(offset, storeListReq);
      boolean isLastPage = (storeListReq.getOffset() + filterStores.size()) >= totalItems;

      // 리턴
      return StoreListRes.builder()
              .stores(filterStores)
              .totalItems(totalItems)
              .isLastPage(isLastPage)
              .build();
   }
}
