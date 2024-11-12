package com.project.domain.tokencheck.entity;

import jakarta.persistence.*;
import kr.co.steellink.user.common.BaseEntity;
import kr.co.steellink.user.common.YN;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "token_check")
public class TokenCheck extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_check_id")
    @Comment("토큰 확인 테이블 아이디")
    private Long id;

    @Comment("jwtToken")
    private String jwtToken;

    @Comment("사용 여부")
    private YN useYn;
}
