package com.project.domain.memberbusinessregion.entity;

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
@Table(name = "member_business_region")
public class MemberBusinessRegion extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mem_bsns_region_id", nullable = false)
    @Comment("회원 관심지역 일련번호")
    private Long id;

    @Column(name = "mem_id")
    private Long memId;

    @Column(name = "delivery_region_id")
    private Long deliveryRegionId;

}
