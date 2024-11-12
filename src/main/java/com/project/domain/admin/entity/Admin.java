package com.project.domain.admin.entity;

import com.project.domain.admin.enumset.MemberRole;
import jakarta.persistence.*;
import kr.co.steellink.user.common.BaseEntity;
import kr.co.steellink.user.common.YN;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Comment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Entity
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Table(name = "admin")
public class Admin extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id")
    @Comment("관리자 일련번호")
    private Long id;

    @Comment("아이디")
    private String loginId;

    @Comment("비밀번호")
    private String pwd;

    @Comment("담당자명")
    private String name;

    @Comment("이메일")
    private String email;

    @Comment("휴대폰 번호")
    private String phoneNo;

    @Comment("권한")
    private String role;

    @Comment("사용 여부")
    private YN status;

    @Comment("마지막 로그인 일시")
    private LocalDateTime lastLoginDt;

    /**
     * 매뉴별 알림톡 수신 여부
     */

    @Comment("매뉴별 알림톡 수신 여부_회원")
    private YN member;

    @Comment("매뉴별 알림톡 수신 여부_견적문의")
    private YN estimate;

    @Comment("매뉴별 알림톡 수신 여부_프리마켓")
    private YN freeMarket;

    @Comment("매뉴별 알림톡 수신 여부_스틸on")
    private YN steelOn;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.unmodifiableList(AuthorityUtils.createAuthorityList(MemberRole.ADMIN.getRole()));
    }

    @Override
    public String getPassword() {
        return this.pwd;
    }

    @Override
    public String getUsername() {
        return this.loginId;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
