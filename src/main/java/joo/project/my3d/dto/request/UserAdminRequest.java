package joo.project.my3d.dto.request;

import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.AddressDto;
import joo.project.my3d.dto.UserAccountDto;

public record UserAdminRequest(
        UserRole userRole,
        String nickname,
        String password,
        String phone,
        String email,
        String detail,
        String street,
        String zipcode
) {
    public UserAccountDto toDto() {
        return UserAccountDto.of(
                email,
                nickname,
                phone,
                AddressDto.of(zipcode, street, detail),
                userRole
        );
    }
}
