package joo.project.my3d.service;

import joo.project.my3d.domain.Company;
import joo.project.my3d.domain.Orders;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.dto.CompanyDto;
import joo.project.my3d.dto.OrdersDto;
import joo.project.my3d.exception.CompanyException;
import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.exception.OrdersException;
import joo.project.my3d.repository.CompanyRepository;
import joo.project.my3d.repository.OrdersRepository;
import joo.project.my3d.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserAccountRepository userAccountRepository;

    public CompanyDto getCompany(Long companyId) {
        return companyRepository.findById(companyId)
                .map(CompanyDto::from)
                .orElseThrow(() -> new CompanyException(ErrorCode.COMPANY_NOT_FOUND));
    }

    @Transactional
    public void saveCompany(CompanyDto dto) {
        try{
            companyRepository.save(dto.toEntity());
        } catch (EntityNotFoundException e) {
            log.warn("기업 저장 실패! - {}", new CompanyException(ErrorCode.FAILED_SAVE));
        }
    }

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
            log.warn("기업 수정 실패! - dto: {} {}", dto, new CompanyException(ErrorCode.COMPANY_NOT_FOUND));
        }
    }

    @Transactional
    public void deleteCompany(Long companyId, String email) {
        UserAccount userAccount = userAccountRepository.findByCompanyId(companyId)
                .orElseThrow(() -> new UsernameNotFoundException("기업 유저를 찾을 수 없습니다."));
        //작성자와 삭제 요청 유저가 같은지 확인
        if (userAccount.getEmail().equals(email)) {
            companyRepository.deleteById(companyId);
        } else {
            log.error("작성자와 삭제 요청 유저가 다릅니다. 작성자: {} - 삭제 요청: {}", userAccount.getEmail(), email);
            throw new CompanyException(ErrorCode.NOT_WRITER);
        }
    }
}
