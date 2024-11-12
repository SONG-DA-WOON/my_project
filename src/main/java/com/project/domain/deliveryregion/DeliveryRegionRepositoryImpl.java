package com.project.domain.deliveryregion;

import com.project.domain.deliveryregion.dto.DeliveryRegionListDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kr.co.steellink.user.common.YN;
import kr.co.steellink.user.domain.deliveryregion.dto.DeliveryRegionListDto;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static kr.co.steellink.user.domain.deliveryregion.entity.QDeliveryRegion.deliveryRegion;
import static org.springframework.util.StringUtils.hasText;

@Repository
public class DeliveryRegionRepositoryImpl implements DeliveryRegionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public DeliveryRegionRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * 지역 선택 팝업 리스트
     * @param search
     * @return
     */
    @Override
    public List<DeliveryRegionListDto> searchDeliveryRegionList(Map<String, Object> search) {
        return queryFactory.select(Projections.fields(DeliveryRegionListDto.class,
                deliveryRegion.id,
                deliveryRegion.regionNm
                ))
                .from(deliveryRegion)
                .where(condDeliverySearch(search), deliveryRegion.status.eq(YN.Y), deliveryRegion.delYn.eq(YN.N))
                .fetch();
    }

    /**
     * 지역 선택 팝업 검색 조건
     * @param search
     * @return
     */
    private Predicate condDeliverySearch(Map<String, Object> search) {
        BooleanBuilder builder = new BooleanBuilder();
        String keyword = search.get("search").toString() == null ? "" : search.get("search").toString();
        if(hasText(keyword)) {
            builder.and(deliveryRegion.regionNm.contains(keyword));
        }

        return builder;
    }
}
