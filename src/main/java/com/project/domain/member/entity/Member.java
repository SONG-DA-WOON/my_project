package com.project.domain.member.entity;

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
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor
@Getter
@Setter
@Table(name = "member")
public class Member extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mem_id")
    @Comment("사용자 일련번호")
    private Long id;

    @Comment("이메일 아이디")
    private String loginId;

    @Comment("회원 이름")
    private String name;

    @Comment("비밀번호")
    private String pwd;

    @Comment("휴대폰번호")
    private String phoneNo;

    @Comment("대표자명")
    private String ceo;

    @Comment("회사명")
    private String company;

    @Comment("은행명")
    private String bank;

    @Comment("계좌번호")
    private String accountNo;

    @Comment("사업자 등록번호")
    private String businessNo;

    @Comment("사업자등록증 첨부파일")
    private Long companyFile;

    @Comment("관심/취급 품목(판재류)")
    private String subPlate;

    @Comment("관심/취급 품목(철근류)")
    private String subSteel;

    @Comment("관심/취급 품목(형강류)")
    private String subSectionsSteel;

    @Comment("관심/취급 품목(강관류)")
    private String subSteelPipe;

    @Comment("회원 이미지")
    private Long profileImg;

    @Comment("푸시토큰")
    private String pushToken;

    @Comment("푸시토큰 디바이스")
    private String pushTokenDevise;

    @Comment("푸시토큰 수정일")
    private LocalDateTime pushTokenModDt;

    @Comment("상태 (정상/정지/삭제)")
    private MemberStatus sttsCd;

    @Comment("시큐리티 권한")
    private String role;

    @Comment("일반/알림 회원")
    private MemberType memType;

    @Comment("푸시여부")
    private YN pushYn;

    @Comment("야간 알림 여부")
    @Column(name = "night_alarm_yn")
    private YN nightAlarmYn;

    @Comment("삭제여부")
    private YN delYn;

    @Comment("노출여부")
    private YN viewYn;

    @Comment("약관동의여부 (만14세)")
    private YN agreeFourteenMoreYn;

    @Comment("약관동의여부 (이용약관)")
    private YN agreeUserTermsYn;

    @Comment("판매글 알람수신여부")
    private YN alarmSellYn;

    @Comment("구매글 알람수신여부")
    private YN alarmBuyYn;

    @Comment("로그인 일시")
    private LocalDateTime loginDt;

    @Comment("탈퇴일")
    private LocalDateTime exitDt;

    @Comment("탈퇴 사유")
    private String exitReason;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.unmodifiableList(AuthorityUtils.createAuthorityList(MemberRole.USER.getRole()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.getLoginId();
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
        return this.delYn == YN.N;
    }
}
