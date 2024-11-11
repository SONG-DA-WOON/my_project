package kr.co.steellink.user.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 모든 Entity 상속받는 공통 추상 클래스<br />
 * 직렬화 저장이 필요할 수도 있으므로 {@link Serializable} 인터페이스가 추가됨<br />
 *
 * @author Tae-rok Hwang
 * @since 2023-05-24
 * @version 1.0.0
 */
@MappedSuperclass
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity implements Serializable {

    @Builder.Default
    @Column(columnDefinition = "CHAR(1)", nullable = false, insertable = false)
    @ColumnDefault("'N'")
    @Comment("삭제 여부")
    protected YN delYn = YN.N;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    @Comment("등록자 ID")
    protected String regId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    protected LocalDateTime regDt;

    @LastModifiedBy
    @Column(insertable = false)
    @Comment("수정자 ID")
    protected String modId;

    @LastModifiedDate
    @Column(insertable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @Comment("수정 시간")
    protected LocalDateTime modDt;

}
