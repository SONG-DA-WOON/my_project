package com.project.domain.member.dto;

import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.index.qual.GTENegativeOne;
import org.hibernate.annotations.Comment;

@Getter
@Setter
public class MemberWithdrawReasonDto {
    @Comment("사유")
    private String reason;
}
