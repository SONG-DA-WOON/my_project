package com.project.domain.memberbusinessregion;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kr.co.steellink.user.common.YN;
import kr.co.steellink.user.domain.deliveryregion.DeliveryRegionRepository;
import kr.co.steellink.user.domain.memberbusinessregion.dto.MemberBusinessRegionListDtoForMemUpdate;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.co.steellink.user.domain.deliveryregion.entity.QDeliveryRegion.deliveryRegion;
import static kr.co.steellink.user.domain.memberbusinessregion.entity.QMemberBusinessRegion.memberBusinessRegion;

@Repository
public class MemberBusinessRegionRepositoryImpl implements MemberBusinessRegionRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final DeliveryRegionRepository deliveryRegionRepository;

    public MemberBusinessRegionRepositoryImpl(EntityManager em, DeliveryRegionRepository deliveryRegionRepository) {
        this.queryFactory = new JPAQueryFactory(em);
        this.deliveryRegionRepository = deliveryRegionRepository;
    }


    @Override
    public List<MemberBusinessRegionListDtoForMemUpdate> searchMemberBizRegion(Long memId) {
        return queryFactory.select(Projections.fields(MemberBusinessRegionListDtoForMemUpdate.class,
                memberBusinessRegion.deliveryRegionId,
                deliveryRegion.regionNm
        ))
                .from(memberBusinessRegion)
                .leftJoin(deliveryRegion).on(memberBusinessRegion.deliveryRegionId.eq(deliveryRegion.id))
                .where(memberBusinessRegion.memId.eq(memId), deliveryRegion.status.eq(YN.Y))
                .fetch();
    }
}
