package joo.project.my3d.dto;

import joo.project.my3d.domain.Company;

public record CompanyDto(Long id, String companyName, String homepage) {
    public static CompanyDto of(Long id, String companyName, String homepage) {
        return new CompanyDto(id, companyName, homepage);
    }

    public static CompanyDto of() {
        return CompanyDto.of(null, null, null);
    }

    public static CompanyDto from(Company company) {
        return new CompanyDto(company.getId(), company.getCompanyName(), company.getHomepage());
    }

    public Company toEntity() {
        return Company.of(companyName, homepage);
    }
}
