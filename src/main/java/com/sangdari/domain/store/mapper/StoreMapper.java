package com.sangdari.domain.store.mapper;

import com.sangdari.domain.store.requests.StoreListReq;
import com.sangdari.domain.store.responses.StoreItems;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface StoreMapper {

    // 현재 페이지의 "필터링된 업체"들의 데이터 리스트
    List<StoreItems> getFilteredStores(StoreListReq storeListReq);

    // 불러온 업체 개수
    long countStores(StoreListReq storeListReq);
}
