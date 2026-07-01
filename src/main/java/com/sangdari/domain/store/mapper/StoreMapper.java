package com.sangdari.domain.store.mapper;

import com.sangdari.domain.store.requests.AllStoreListReq;
import com.sangdari.domain.store.requests.StoreListReq;
import com.sangdari.domain.store.responses.StoreDetailItems;
import com.sangdari.domain.store.responses.StoreItems;
import com.sangdari.domain.store.responses.StoreMenuItems;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StoreMapper {

    // 현재 페이지의 "전체 업체 목록"들의 데이터 리스트
    List<StoreItems> getAllStores(@Param("offset") int offset, @Param("allStoreListReq") AllStoreListReq allStoreListReq);

    // 잔체 업체 개수
    long getTotalStoreCount();

    // 현재 페이지의 "필터링된 업체"들의 데이터 리스트
    List<StoreItems> getFilteredStores(@Param("offset") int offset, @Param("storeListReq") StoreListReq storeListReq);

    // 불러온 업체 개수
    long countStores(@Param("offset") int offset, @Param("storeListReq") StoreListReq storeListReq);

    // 업체 상세 기본 정보
    StoreDetailItems getStoreDetail(@Param("storeId") Long storeId);

    // 업체 상세 메뉴 목록
    List<StoreMenuItems> getStoreMenus(@Param("storeId") Long storeId);
}
