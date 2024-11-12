package com.project.domain.memberbusinessitem;

import kr.co.steellink.user.domain.memberbusinessitem.entity.MemberBusinessItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;


public interface MemberBusinessItemRepository extends
        JpaRepository<MemberBusinessItem, Long>,
        QuerydslPredicateExecutor<MemberBusinessItem>,
        MemberBusinessItemRepositoryCustom {


}
