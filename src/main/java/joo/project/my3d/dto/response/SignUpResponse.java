package joo.project.my3d.dto.response;

import joo.project.my3d.domain.Address;
import joo.project.my3d.domain.Company;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.UserRole;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public record SignUpResponse(
        UserRole userRole,
        String companyName,
        String nickname,
        String password,
        String zipcode,
        String address,
        String detailAddress
) {

    public static SignUpResponse of(UserRole userRole, String companyName, String nickname, String password, String zipcode, String address, String detailAddress) {
        return new SignUpResponse(userRole, companyName, nickname, password, zipcode, address, detailAddress);
    }
}
