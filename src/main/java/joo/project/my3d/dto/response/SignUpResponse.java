package joo.project.my3d.dto.response;

import joo.project.my3d.domain.constant.UserRole;

public record SignUpResponse(
        UserRole userRole,
        String companyName,
        String nickname,
        String password,
        String zipcode,
        String address,
        String detailAddress
) {

    public static SignUpResponse of(UserRole userRole, String nickname) {
        return new SignUpResponse(userRole, null, nickname, null, null, null, null);
    }
}
