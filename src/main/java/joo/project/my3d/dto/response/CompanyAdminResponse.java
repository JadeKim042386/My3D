package joo.project.my3d.dto.response;

import joo.project.my3d.dto.CompanyDto;

public record CompanyAdminResponse(
        String companyName,
        String homepage
) {
    public static CompanyAdminResponse of(String companyName, String homepage) {
        return new CompanyAdminResponse(companyName, homepage);
    }

    public static CompanyAdminResponse from(CompanyDto dto) {
        return CompanyAdminResponse.of(
                dto.companyName(),
                dto.homepage()
        ) ;
    }
}
