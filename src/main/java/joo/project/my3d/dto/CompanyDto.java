package joo.project.my3d.dto;

import joo.project.my3d.domain.Company;

public record CompanyDto(
        String companyName,
        String homepage
) {
    public static CompanyDto of() {
        return new CompanyDto(null, null);
    }

    public static CompanyDto of(String companyName, String homepage) {
        return new CompanyDto(companyName, homepage);
    }

    public static CompanyDto from(Company company) {
        return new CompanyDto(
                company.getCompanyName(),
                company.getHomepage()
        );
    }

    public Company toEntity() {
        return Company.of(companyName, homepage);
    }

}
