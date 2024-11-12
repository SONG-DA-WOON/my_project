package com.project.domain.memberbusinessregion.dto;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Setter
public class MemberBusinessRegionListDtoForMemUpdate {

    @Comment("지역 일련번호")
    private Long deliveryRegionId;

    private String regionNm;
}
