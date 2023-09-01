package joo.project.my3d.config;

import joo.project.my3d.config.handler.CustomOAuth2SuccessHandler;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.dto.properties.JwtProperties;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.service.UserAccountService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;

@Import(SecurityConfig.class)
@EnableConfigurationProperties(value = JwtProperties.class)
public class TestSecurityConfig {
    @MockBean private UserAccountService userAccountService;
    @MockBean private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @BeforeTestMethod
    public void securitySetUp() {
        given(userAccountService.searchUser("jooCompany"))
                .willReturn(Optional.of(FixtureDto.getUserAccountDto("jooCompany", UserRole.COMPANY, true)));
        given(userAccountService.searchUser("jooUser"))
                .willReturn(Optional.of(FixtureDto.getUserAccountDto("jooUser", UserRole.USER, true)));
        given(userAccountService.searchUser("notSignedJooUser"))
                .willReturn(Optional.of(FixtureDto.getUserAccountDto("notSignedJooUser", UserRole.USER, false)));
        given(userAccountService.searchUser("jooAdmin"))
                .willReturn(Optional.of(FixtureDto.getUserAccountDto("jooAdmin", UserRole.ADMIN, true)));
        given(userAccountService.saveUser(anyString(), anyString(), anyString(), any(UserRole.class), anyBoolean()))
                .willReturn(createUserAccountDto());
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                "joo-test@gmail.com",
                "pw",
                "joo-test",
                true,
                UserRole.COMPANY
        );
    }
}
