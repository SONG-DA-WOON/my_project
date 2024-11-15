package com.project.domain.member;

import com.project.domain.member.entity.Member;
import com.project.domain.member.enumset.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, QuerydslPredicateExecutor<Member>, kr.co.steellink.user.domain.member.MemberRepositoryCustom {

    Member findByLoginId(String username);

    Member findByLoginIdAndSttsCd(String name, MemberStatus memberStatus);

    Member findByLoginIdAndPhoneNo(String loginId, String phoneNo);

    @Query("SELECT m.company FROM Member m WHERE m.id = :memId")
    String findCompanyByMemId(Long memId);

    @Query("SELECT m.subPlate FROM Member m WHERE m.id = :memId")
    String findSubPlateByMemId(@Param("memId") Long memId);

    @Query("SELECT m.subSteel FROM Member m WHERE m.id = :memId")
    String findSubSteelByMemId(@Param("memId") Long memId);

    @Query("SELECT m.subSectionsSteel FROM Member m WHERE m.id = :memId")
    String findSubSectionsSteelByMemId(@Param("memId") Long memId);

    @Query("SELECT m.subSteelPipe FROM Member m WHERE m.id = :memId")
    String findSubSteelPipeByMemId(@Param("memId") Long memId);



}
