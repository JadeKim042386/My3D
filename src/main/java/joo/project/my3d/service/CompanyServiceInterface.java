package joo.project.my3d.service;

import joo.project.my3d.dto.CompanyDto;

public interface CompanyServiceInterface {
    /**
     * 기업 조회
     */
    CompanyDto getCompany(String email);
    /**
     * 기업 존재 유무 조회
     */
    boolean isExistsByCompanyName(String companyName);

    /**
     * 기업 정보 수정
     */
    void updateCompany(CompanyDto dto);
}
