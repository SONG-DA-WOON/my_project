package com.project.domain.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberPasswordUpdateDto {

    private String currentPassword;

    private String newPassword;

    private String passwordCheck;
}
