package com.project.domain.member.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import kr.co.steellink.user.domain.memberbusinessitem.dto.MemberBusinessItemDto;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Setter
public class MemberUpdateDto {

    @Comment("로그인 아이디")

    private String loginId;

    @Comment("비밀번호")
    private String password;

    @Comment("비밀번호 확인 용")
    private String passwordCheck;

    @Comment("휴대폰 번호")
    private String phoneNumber;

    @Comment("사업자 등록 번호")
    private String businessNumber;

    @Comment("대표자명")
    private String ceo;

    @Comment("회사명")
    private String company;

    @Comment("은행명")
    private String bank;

    @Comment("계좌번호")
    private String accountNo;

    // db에 넣을 떄는 Long으로 변경하기
    @Comment("사업자등록증 첨부파일")
    private String companyFile;

    @Comment("판재류")
    private String plate;

    @Comment("철근류")
    private String rebar;

    @Comment("형강류")
    private String section;

    @Comment("강관류")
    private String pipe;

    @Comment("구매/판매 지역")
    private String region;

    @Comment("회원 이미지")
    private Long profileImg;

}
