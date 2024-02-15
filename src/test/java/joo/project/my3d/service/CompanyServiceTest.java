package joo.project.my3d.service;

import joo.project.my3d.domain.Company;
import joo.project.my3d.dto.CompanyDto;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.repository.CompanyRepository;
import joo.project.my3d.repository.UserAccountRepository;
import joo.project.my3d.service.impl.CompanyService;
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
    @InjectMocks
    private CompanyService companyService;

    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private UserAccountRepository userAccountRepository;

    @DisplayName("기업 조회")
    @Test
    void getCompany() {
        // Given
        Long userAccountId = 1L;
        Company company = Fixture.getCompany();
        given(userAccountRepository.findCompanyById(anyLong())).willReturn(Optional.of(company));
        // When
        companyService.getCompanyEntity(userAccountId);
        // Then
    }

    @DisplayName("기업 수정")
    @Test
    void updateCompany() throws IllegalAccessException {
        // Given
        CompanyDto companyDto = FixtureDto.getCompanyDto();
        Long userAccountId = 1L;
        given(userAccountRepository.findCompanyById(anyLong())).willReturn(Optional.of(companyDto.toEntity()));
        // When
        companyService.updateCompany(FixtureDto.getCompanyAdminRequest(), userAccountId);
        // Then
    }
}
