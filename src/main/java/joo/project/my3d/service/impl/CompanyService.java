package joo.project.my3d.service.impl;

import joo.project.my3d.domain.Company;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.dto.CompanyDto;
import joo.project.my3d.dto.request.CompanyAdminRequest;
import joo.project.my3d.exception.AuthException;
import joo.project.my3d.exception.CompanyException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.repository.CompanyRepository;
import joo.project.my3d.repository.UserAccountRepository;
import joo.project.my3d.service.CompanyServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompanyService implements CompanyServiceInterface {

    private final UserAccountRepository userAccountRepository;
    private final CompanyRepository companyRepository;

    @Override
    public CompanyDto getCompanyDto(Long userAccountId) {
        return CompanyDto.from(getCompanyEntity(userAccountId));
    }

    @Override
    public Company getCompanyEntity(Long userAccountId) {
        return userAccountRepository
                .findById(userAccountId)
                .map(UserAccount::getCompany)
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
    public CompanyDto updateCompany(CompanyAdminRequest request, Long userAccountId) {
        try {
            Company company = getCompanyEntity(userAccountId);
            Optional.ofNullable(request.getCompanyName()).ifPresent(company::setCompanyName);
            Optional.ofNullable(request.getHomepage()).ifPresent(company::setHomepage);
            return CompanyDto.from(company);
        } catch (EntityNotFoundException e) {
            throw new AuthException(ErrorCode.NOT_FOUND_COMPANY, e);
        }
    }
}
