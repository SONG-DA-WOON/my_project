package kr.co.steellink.user.domain.member;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import kr.co.steellink.user.common.YN;
import kr.co.steellink.user.domain.member.dto.MemberDetailDto;
import kr.co.steellink.user.domain.member.dto.MemberFindIdDto;
import org.springframework.stereotype.Repository;

import java.util.List;

import static kr.co.steellink.user.domain.member.entity.QMember.member;
import static kr.co.steellink.user.domain.memberpoint.entity.QMemberPoint.memberPoint;

@Repository
public class MemberRepositoryImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    public MemberRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    /**
     * 아이디 찾기 데이터
     * @param dto
     * @return
     */
    @Override
    public List<MemberFindIdDto> searchMemberByNameAndPhoneNo(MemberFindIdDto dto) {
        return queryFactory
                .select(Projections.fields(MemberFindIdDto.class,
                    member.loginId))
                .from(member)
                .where(member.name.eq(dto.getMemName()).and(member.phoneNo.eq(dto.getPhoneNo())))
                .fetch();
    }

    /**
     * 회원 상세
     * @param memberId
     * @return
     */
    @Override
    public MemberDetailDto searchMemberDetail(Long memberId) {
        return queryFactory
                .select(Projections.fields(MemberDetailDto.class,
                        member.id.as("memId"),
                        member.loginId,
                        member.name,
                        member.phoneNo,
                        member.ceo,
                        member.company,
                        member.bank,
                        member.accountNo,
                        member.businessNo,
                        member.companyFile,
                        member.sttsCd,
                        member.memType,
                        member.subPlate,
                        member.subSteel,
                        member.subSectionsSteel,
                        member.subSteelPipe,
                        member.alarmBuyYn,
                        member.alarmSellYn,
                        member.loginDt,
                        member.regDt.as("joinDt"),
                        member.exitDt
                ))
                .from(member)
                .where(member.id.eq(memberId))
                .fetchFirst();

    }

}
