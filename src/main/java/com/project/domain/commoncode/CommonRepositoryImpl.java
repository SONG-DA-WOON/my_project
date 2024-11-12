package com.project.domain.commoncode;

import com.project.domain.commoncode.dto.BankInfoDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kr.co.steellink.user.domain.commoncode.dto.BankInfoDto;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.co.steellink.user.domain.commoncode.entity.QCommonCode.commonCode;


@Repository
public class CommonRepositoryImpl implements CommonRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public CommonRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


    @Override
    public List<BankInfoDto> searchBankInfo() {
        return queryFactory
                .select(
                        Projections.fields(BankInfoDto.class,
                                commonCode.detailCd,
                                commonCode.detailNm
                        ))
                .from(commonCode)
                .where(commonCode.groupCd.eq("BANK"))
                .fetch();
    }
}
