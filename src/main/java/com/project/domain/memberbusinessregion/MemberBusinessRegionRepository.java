package com.project.domain.memberbusinessregion;

import kr.co.steellink.user.domain.memberbusinessregion.entity.MemberBusinessRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface MemberBusinessRegionRepository extends JpaRepository<MemberBusinessRegion, Long>,
        QuerydslPredicateExecutor<MemberBusinessRegion>,
        MemberBusinessRegionRepositoryCustom {

    List<MemberBusinessRegion> findByMemId(Long memId);
}
