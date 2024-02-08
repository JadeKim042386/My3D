package joo.project.my3d.service;

import joo.project.my3d.domain.Company;
import joo.project.my3d.dto.CompanyDto;
import joo.project.my3d.dto.request.CompanyAdminRequest;

public interface CompanyServiceInterface {
    /**
     * 기업 DTO 조회
     */
    CompanyDto getCompanyDto(String email);
    /**
     * 기업 Entity 조회
     */
    Company getCompany(String email);
    /**
     * 기업 존재 유무 조회
     */
    boolean isExistsByCompanyName(String companyName);

    /**
     * 기업 정보 수정
     */
    CompanyDto updateCompany(CompanyAdminRequest request, String email);
}
