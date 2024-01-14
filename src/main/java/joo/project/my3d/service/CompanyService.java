package joo.project.my3d.service;

import joo.project.my3d.domain.Company;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.dto.CompanyDto;
import joo.project.my3d.exception.AuthException;
import joo.project.my3d.exception.CompanyException;
import joo.project.my3d.exception.constant.AuthErrorCode;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.repository.CompanyRepository;
import joo.project.my3d.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserAccountRepository userAccountRepository;

    public CompanyDto getCompany(String email) {
        return companyRepository.findByUserAccount_Email(email)
                .map(CompanyDto::from)
                .orElseThrow(() -> new AuthException(AuthErrorCode.NOT_FOUND_COMPANY));
    }

    public boolean isExistsByCompanyName(String companyName) {
        return companyRepository.existsByCompanyName(companyName);
    }

    /**
     * @throws CompanyException 기업 정보 저장 실패했을 경우 발생하는 예외
     */
    @Transactional
    public void saveCompany(CompanyDto dto) {
        try{
            companyRepository.save(dto.toEntity());
        } catch (IllegalArgumentException e) {
            throw new CompanyException(ErrorCode.FAILED_SAVE, e);
        } catch (OptimisticLockingFailureException e) {
            throw new CompanyException(ErrorCode.CONFLICT_SAVE, e);
        }
    }

    /**
     * @throws CompanyException 기업이 존재하지 않는 경우 발생하는 예외
     */
    @Transactional
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
            throw new AuthException(AuthErrorCode.NOT_FOUND_COMPANY, e);
        }
    }

    /**
     * @throws UsernameNotFoundException 기업 유저를 찾을 수 없을 경우 발생하는 예외
     * @throws CompanyException 대상 유저와 삭제 요청 유저가 다르거나 삭제에 실패했을 경우 발생하는 예외
     */
    @Transactional
    public void deleteCompany(Long companyId, String email) {
        UserAccount userAccount = userAccountRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new UsernameNotFoundException("기업 유저를 찾을 수 없습니다."));
        //작성자와 삭제 요청 유저가 같은지 확인
        if (userAccount.getEmail().equals(email)) {
            try {
                companyRepository.deleteById(companyId);
            } catch (IllegalArgumentException e) {
                throw new CompanyException(ErrorCode.FAILED_DELETE, e);
            }
        } else {
            log.error("대상 기업의 유저와 삭제 요청 유저가 다릅니다. 작성자: {} - 삭제 요청: {}", userAccount.getEmail(), email);
            throw new CompanyException(ErrorCode.NOT_WRITER);
        }
    }
}
