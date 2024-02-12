package joo.project.my3d.service.impl;

import joo.project.my3d.domain.Company;
import joo.project.my3d.dto.CompanyDto;
import joo.project.my3d.dto.request.CompanyAdminRequest;
import joo.project.my3d.exception.AuthException;
import joo.project.my3d.exception.CompanyException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.repository.CompanyRepository;
import joo.project.my3d.service.CompanyServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompanyService implements CompanyServiceInterface {

    private final CompanyRepository companyRepository;

    @Override
    public CompanyDto getCompanyDto(String email) {
        return CompanyDto.from(getCompany(email));
    }

    @Override
    public Company getCompany(String email) {
        return companyRepository
                .findByUserAccount_Email(email)
                .orElseThrow(() -> new AuthException(ErrorCode.NOT_FOUND_COMPANY));
    }

    @Override
    public boolean isExistsByCompanyName(String companyName) {
        return companyRepository.existsByCompanyName(companyName);
    }

    /**
     * @throws CompanyException 기업이 존재하지 않는 경우 발생하는 예외
     */
    @Transactional
    @Override
    public CompanyDto updateCompany(CompanyAdminRequest request, String email) {
        try {
            Company company = getCompany(email);
            if (request.getCompanyName() != null) {
                company.setCompanyName(request.getCompanyName());
            }
            if (request.getHomepage() != null) {
                company.setHomepage(request.getHomepage());
            }
            return CompanyDto.from(company);
        } catch (EntityNotFoundException e) {
            throw new AuthException(ErrorCode.NOT_FOUND_COMPANY, e);
        }
    }
}
