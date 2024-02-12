package joo.project.my3d.dto.response;

import joo.project.my3d.dto.AddressDto;

public record UserAdminResponse(
        String nickname, String phone, String email, String detail, String street, String zipcode) {
    public static UserAdminResponse of(String nickname, String phone, String email, AddressDto address) {
        return new UserAdminResponse(nickname, phone, email, address.detail(), address.street(), address.zipcode());
    }
}
