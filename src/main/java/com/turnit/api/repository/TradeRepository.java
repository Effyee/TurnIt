package com.turnit.api.repository;

import com.turnit.api.domain.Trade;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {

    /**
     * 특정 시간 이후에 가장 많이 거래된 상품 ID 목록을 조회합니다.
     *
     * @param pageable 조회할 개수 (상위 N개)
     * @return 인기 상품 ID 목록
     */
    @Query("SELECT t.product.id FROM Trade t WHERE t.createdAt >= :since GROUP BY t.product.id ORDER BY COUNT(t.id) DESC")
    List<Long> findPopularProductIds(@Param("since") LocalDateTime since, Pageable pageable);
}
