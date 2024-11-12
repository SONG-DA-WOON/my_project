package com.project.domain.member.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Setter
public class MemberDetailDto {

    @Comment("사용자 일련번호")
    private Long memId;

    @Comment("이메일 아이디")
    private String loginId;

    @Comment("회원명")
    private String name;

    @Comment("휴대폰번호")
    private String phoneNo;

    @Comment("대표자명")
    private String ceo;

    @Comment("회사명")
    private String company;

    @Comment("은행")
    private String bank;

    @Comment("계좌번호")
    private String accountNo;

    @Comment("사업자 등록번호")
    private String businessNo;

    @Comment("관심/취급 품목(판재류)")
    private String subPlate;

    @Comment("관심/취급 품목(철근류)")
    private String subSteel;

    @Comment("관심/취급 품목(형강류)")
    private String subSectionsSteel;

    @Comment("관심/취급 품목(강관류)")
    private String subSteelPipe;

//    @Comment("관심/취급 품목(판재류)")
//    private List<CommonCodeListDto> subPlateList;
//
//    @Comment("관심/취급 품목(철근류)")
//    private List<CommonCodeListDto> subSteelList;
//
//    @Comment("관심/취급 품목(형강류)")
//    private List<CommonCodeListDto> subSectionsSteelList;
//
//    @Comment("관심/취급 품목(강관류)")
//    private List<CommonCodeListDto> subSteelPipeList;


//    @Comment("회원이력")
//    private List<MemberStatusHistoryDto> memHistoryList;

    @Comment("관심지역")
    private String businessRegions;

}
