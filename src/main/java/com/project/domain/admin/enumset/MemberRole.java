package com.project.domain.admin.enumset;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;

@Getter
@AllArgsConstructor
public enum MemberRole {

    NONE(null, "", 0),
    USER("ROLE_USER", "사용자", 1),
    PARTNER("ROLE_PARTNER", "협력점", 2),
    ADMIN("ROLE_ADMIN", "관리자", 3),
    SHOP("ROLE_SHOP", "쇼핑몰", 4);

    private final String role;
    private final String name;
    private final int value;

    public static MemberRole ofRole(String role) {
        return EnumSet.allOf(MemberRole.class).stream()
                .filter(v -> v != MemberRole.NONE && v.getRole().equals(role))
                .findAny()
                .orElse(MemberRole.NONE);
    }

    @Override
    public String toString() {
        return this.getRole();
    }

    @Converter
    public static class MemberRoleConverter implements AttributeConverter<MemberRole, String> {
        @Override
        public String convertToDatabaseColumn(MemberRole attribute) {
            return attribute != null ? attribute.getRole() : MemberRole.NONE.getRole();
        }

        @Override
        public MemberRole convertToEntityAttribute(String dbData) {
            return MemberRole.ofRole(dbData);
        }
    }

}
