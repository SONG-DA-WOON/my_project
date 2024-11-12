package com.project.domain.member.enumset;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;

@Getter
@AllArgsConstructor
public enum MemberType {

    NONE(null, "", 0),
    NORMAL("NORMAL", "일반회원", 1),
    ALARM("ALARM", "알람회원", 2);

    private final String code;
    private final String detail;
    private final int value;

    public static MemberType ofCode(String code) {
        return EnumSet.allOf(MemberType.class).stream()
                .filter(v -> v != MemberType.NONE && v.getCode().equals(code))
                .findAny()
                .orElse(MemberType.NONE);
    }

    @Override
    public String toString() {
        return this.getCode();
    }

    @Converter(autoApply = true)
    public static class MemberTypeConverter implements AttributeConverter<MemberType, String> {
        @Override
        public String convertToDatabaseColumn(MemberType attribute) {
            return attribute != null ? attribute.getCode() : MemberType.NONE.getCode();
        }

        @Override
        public MemberType convertToEntityAttribute(String dbData) {
            return MemberType.ofCode(dbData);
        }
    }

}
