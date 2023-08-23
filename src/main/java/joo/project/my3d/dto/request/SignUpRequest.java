package joo.project.my3d.dto.request;

import joo.project.my3d.domain.Address;
import joo.project.my3d.domain.Company;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.UserRole;

public record SignUpRequest(
        UserRole userRole,
        String companyName,
        String nickname,
        String password,
        String zipcode,
        String address,
        String detailAddress
) {

    public UserAccount toEntity(String email) {
        return UserAccount.of(
                email,
                password,
                nickname,
                null,
                Address.of(
                        zipcode,
                        address,
                        detailAddress
                ),
                true,
                userRole,
                Company.of(
                        companyName,
                        null
                ),
                email
        );
    }
}
