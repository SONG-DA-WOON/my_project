package com.project.domain.memberbusinessitem.entity;

import jakarta.persistence.*;
import kr.co.steellink.user.common.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Getter
@Setter
@Table(name = "member_business_item")
public class MemberBusinessItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mem_bsns_item_id")
    @Comment("사용자 관심/취급 상품 일련번호")
    private Long id;

    @Comment("사용자 아이디")
    private Long memId;

    @Comment("품목 그룹 코드")
    private String itemGroupCd;

    @Comment("품목 상세 코드")
    private String itemDetailCd;
}
