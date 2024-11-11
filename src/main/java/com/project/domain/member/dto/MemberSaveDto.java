package kr.co.steellink.user.domain.member.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import kr.co.steellink.user.domain.memberbusinessitem.dto.MemberBusinessItemDto;
import lombok.Getter;
import org.hibernate.annotations.Comment;

import java.util.Map;

/**
 * 회원 저장
 */
@Getter
public class MemberSaveDto {
    @Comment("로그인 아이디")
    @NotEmpty(message = "아이디를 입력해주세요.")
    private String loginId;

    @Comment("비밀번호")
    @NotEmpty(message = "비밀번호를 입력해주세요.")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,12}$",
            message = "비밀번호는 8~12자의 영문 대소문자, 숫자, 특수문자를 포함해야 합니다."
    )
    private String password;


    @Comment("회원 명")
    @NotEmpty(message = "회원 명을 입력해주세요.")
    private String memberName;

    @Comment("비밀번호 확인 용")
    private String passwordCheck;
    @Comment("휴대폰 번호")
    @NotEmpty(message = "휴대폰 번호를 입력해주세요.")
    private String phoneNumber;
    @Comment("사업자 등록 번호")
    @NotEmpty(message = "사업자 등록번호를 입력해주세요.")
    private String businessNumber;
    @Comment("대표자 명")
    private String ceo;
    @Comment("회사명")
    private String company;
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
    @Comment("판매글 알림 수신")
    private String alarmSellYn;
    @Comment("구매글 알림 수신")
    private String alarmBuyYn;
    @Comment("이미지 아이디")
    private Long fileId;
    @Comment("선택한 구매/판매 목록 하나")
    private MemberBusinessItemDto categories;
}
