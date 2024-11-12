package com.project.domain.member.enumset;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;

@Getter
@AllArgsConstructor
public enum MemberStatus {

    NONE(null, "", 0),
    WAIT("ST001", "대기", 1),
    NORMAL("ST002", "정상", 2),
    HOLD("ST003", "정지", 3),
    WITHDRAW("ST004", "탈퇴", 4);

    private final String code;
    private final String detail;
    private final int value;

    public static MemberStatus ofCode(String code) {
        return EnumSet.allOf(MemberStatus.class).stream()
                .filter(v -> v != MemberStatus.NONE && v.getCode().equals(code))
                .findAny()
                .orElse(MemberStatus.NONE);
    }

    @Override
    public String toString() {
        return this.getCode();
    }

    @Converter(autoApply = true)
    public static class MemberStatusConverter implements AttributeConverter<MemberStatus, String> {
        @Override
        public String convertToDatabaseColumn(MemberStatus attribute) {
            return attribute != null ? attribute.getCode() : MemberStatus.NONE.getCode();
        }

        @Override
        public MemberStatus convertToEntityAttribute(String dbData) {
            return MemberStatus.ofCode(dbData);
        }
    }
}
