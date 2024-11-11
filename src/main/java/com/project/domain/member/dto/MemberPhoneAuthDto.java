package kr.co.steellink.user.domain.member.dto;

import lombok.Getter;

@Getter
public class MemberPhoneAuthDto {
    // 휴대폰 번호
    private String phoneNumber;
    private boolean isRefresh;  // 새로고침 여부 판단을 위한 필드 추가

}
