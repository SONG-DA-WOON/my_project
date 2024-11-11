package kr.co.steellink.user.domain.member;

import kr.co.steellink.user.domain.member.dto.MemberDetailDto;
import kr.co.steellink.user.domain.member.dto.MemberFindIdDto;
import kr.co.steellink.user.domain.member.dto.MemberFindPasswordDto;
import kr.co.steellink.user.domain.member.entity.Member;

import java.util.List;

public interface MemberRepositoryCustom {
    List<MemberFindIdDto> searchMemberByNameAndPhoneNo(MemberFindIdDto dto);

    MemberDetailDto searchMemberDetail(Long memberId);

}
