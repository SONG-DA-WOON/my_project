package kr.co.steellink.user.domain.deliveryregion.entity;

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
@Table(name = "delivery_region")
public class DeliveryRegion extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_region_id")
    @Comment("배송지설정 일련번호")
    private Long id;

    @Comment("배송지코드")
    private String regionCd;

    @Comment("배송지명")
    private String regionNm;

    @Comment("주소")
    private String addr;

    @Comment("상세주소")
    private String addrDetail;

    @Comment("상태")
    private YN status;

}
