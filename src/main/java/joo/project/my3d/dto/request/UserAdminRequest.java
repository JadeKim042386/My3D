package joo.project.my3d.dto.request;

import joo.project.my3d.dto.AddressDto;
import joo.project.my3d.dto.UserAccountDto;
import org.springframework.util.StringUtils;

public record UserAdminRequest(
        String nickname, String password, String phone, String email, String detail, String street, String zipcode) {
    public UserAccountDto toDto() {
        return UserAccountDto.of(
                email,
                nickname,
                phone,
                StringUtils.hasText(zipcode + street + detail) ? AddressDto.of(zipcode, street, detail) : null);
    }
}
