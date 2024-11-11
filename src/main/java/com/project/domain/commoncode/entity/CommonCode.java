package kr.co.steellink.user.domain.commoncode.entity;

import jakarta.persistence.*;
import kr.co.steellink.user.common.YN;
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
@Table(name = "common_code")
public class CommonCode extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_id")
    @Comment("공통코드 일련번호")
    private Long id;

    @Comment("그룹코드")
    private String groupCd;

    @Comment("그룹코드명")
    private String groupNm;

    @Comment("상세코드")
    private String detailCd;

    @Comment("상세코드명")
    private String detailNm;

    @Comment("상세값")
    private String detailVal;

    @Comment("상세옵션")
    private String detailOpt;

    @Comment("상세옵션2")
    @Column(name = "detail_opt_2")
    private String detailOpt2;

    @Comment("상세코드 단위중량 값")
    private String detailUnit;

    @Comment("상위그룹코드")
    private String upperGroupCd;

    @Comment("레벨")
    private String level;

    @Comment("삭제여부")
    private YN delYn;

    @Comment("노출여부")
    private YN viewYn;

}
