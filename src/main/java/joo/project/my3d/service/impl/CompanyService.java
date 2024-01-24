package joo.project.my3d.service.impl;

import joo.project.my3d.domain.Company;
import joo.project.my3d.dto.CompanyDto;
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
    public CompanyDto getCompany(String email) {
        return companyRepository.findByUserAccount_Email(email)
                .map(CompanyDto::from)
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
    public void updateCompany(CompanyDto dto) {
        try {
            Company company = companyRepository.getReferenceById(dto.id());
            if (dto.companyName() != null) {
                company.setCompanyName(dto.companyName());
            }
            if (dto.homepage() != null) {
                company.setHomepage(dto.homepage());
            }
        } catch (EntityNotFoundException e) {
            throw new AuthException(ErrorCode.NOT_FOUND_COMPANY, e);
        }
    }
}
