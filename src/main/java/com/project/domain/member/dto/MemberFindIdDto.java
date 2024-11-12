package com.project.domain.member.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Setter
public class MemberFindIdDto {
    @Comment("이름")
    private String memName;

    @Comment("휴대폰번호")
    private String phoneNo;

    @Comment("로그인 아이디")
    private String loginId;
}
