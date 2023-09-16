package joo.project.my3d.dto.request;

import joo.project.my3d.dto.CompanyDto;

public record CompanyAdminRequest(
        String companyName,
        String homepage
) {
    public CompanyDto toDto() {
        return CompanyDto.of(
                companyName,
                homepage
        );
    }
}
