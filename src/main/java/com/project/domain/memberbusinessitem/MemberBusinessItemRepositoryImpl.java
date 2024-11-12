package com.project.domain.memberbusinessitem;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;


@Repository
public class MemberBusinessItemRepositoryImpl implements MemberBusinessItemRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public MemberBusinessItemRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }


}
