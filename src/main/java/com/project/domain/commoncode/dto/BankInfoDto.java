package com.project.domain.commoncode.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Setter
public class BankInfoDto {

    @Comment("그룹코드")
    private String groupCd;

    @Comment("영문 은행이름")
    private String detailCd;

    @Comment("한글 은행이름")
    private String detailNm;
}
