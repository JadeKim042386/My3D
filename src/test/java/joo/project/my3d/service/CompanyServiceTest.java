package joo.project.my3d.service;

import joo.project.my3d.domain.Company;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.dto.CompanyDto;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.repository.CompanyRepository;
import joo.project.my3d.repository.UserAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@DisplayName("비지니스 로직 - 기업")
@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {
    @InjectMocks private CompanyService companyService;
    @Mock private CompanyRepository companyRepository;
    @Mock private UserAccountRepository userAccountRepository;

    @DisplayName("기업 조회")
    @Test
    void getCompany() {
        // Given
        Company company = Fixture.getCompany();
        given(companyRepository.findById(company.getId())).willReturn(Optional.of(company));
        // When
        companyService.getCompany(company.getId());
        // Then
        then(companyRepository).should().findById(company.getId());
    }

    @DisplayName("기업 저장(추가)")
    @Test
    void saveCompany() {
        // Given
        CompanyDto companyDto = FixtureDto.getCompanyDto();
        given(companyRepository.save(any(Company.class))).willReturn(any(Company.class));
        // When
        companyService.saveCompany(Fixture.getUserAccount(), companyDto);
        // Then
        then(companyRepository).should().save(any(Company.class));
    }

    @DisplayName("기업 수정")
    @Test
    void updateCompany() {
        // Given
        CompanyDto companyDto = FixtureDto.getCompanyDto();
        given(companyRepository.getReferenceById(companyDto.id())).willReturn(companyDto.toEntity());
        // When
        companyService.updateCompany(companyDto);
        // Then
        then(companyRepository).should().getReferenceById(companyDto.id());
    }

    @DisplayName("기업 삭제")
    @Test
    void deleteCompany() {
        // Given
        Long companyId = 1L;
        UserAccount userAccount = Fixture.getUserAccount();
        given(userAccountRepository.findByCompanyId(companyId)).willReturn(Optional.of(userAccount));
        willDoNothing().given(companyRepository).deleteById(companyId);
        // When
        companyService.deleteCompany(companyId, userAccount.getEmail());
        // Then
        then(userAccountRepository).should().findByCompanyId(companyId);
        then(companyRepository).should().deleteById(companyId);
    }
}
