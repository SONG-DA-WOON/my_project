package kr.co.steellink.user.domain.member.dto;

import lombok.Data;
import org.hibernate.annotations.Comment;

@Data
public class MemberFindPasswordDto {
    @Comment("로그인 아이디")
    private String loginId;
    @Comment("휴대폰 번호")
    private String phoneNo;
}
