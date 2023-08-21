package joo.project.my3d.domain.constant;

import lombok.Getter;

public enum UserRole {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    COMPANY("ROLE_COMPANY"),
    ANONYMOUS("ROLE_ANONYMOUS")
    ;

    @Getter
    private final String name;

    UserRole(String name) {
        this.name = name;
    }
}
