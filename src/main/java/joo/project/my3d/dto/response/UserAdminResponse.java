package joo.project.my3d.dto.response;

import joo.project.my3d.domain.Address;
import joo.project.my3d.domain.constant.UserRole;

public record UserAdminResponse(
        UserRole userRole,
        String nickname,
        String phone,
        String email,
        String detail,
        String street,
        String zipcode
) {
    public static UserAdminResponse of(UserRole userRole, String nickname, String phone, String email, Address address) {
        return new UserAdminResponse(userRole, nickname, phone, email, address.getDetail(), address.getStreet(), address.getZipcode());
    }
}
