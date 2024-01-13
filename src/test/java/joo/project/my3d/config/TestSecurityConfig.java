package joo.project.my3d.config;

import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.security.CustomOAuth2SuccessHandler;
import joo.project.my3d.security.SecurityConfig;
import joo.project.my3d.security.TokenProvider;
import joo.project.my3d.service.UserAccountService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;

@Import(SecurityConfig.class)
public class TestSecurityConfig {
    @MockBean private TokenProvider tokenProvider;
    @MockBean private UserAccountService userAccountService;
    @MockBean private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @BeforeTestMethod
    public void securitySetUp() {
        given(userAccountService.searchUser("jooCompany@gmail.com"))
                .willReturn(FixtureDto.getUserAccountDto("jooCompany", UserRole.COMPANY, true));
        given(userAccountService.searchUser("jooUser@gmail.com"))
                .willReturn(FixtureDto.getUserAccountDto("jooUser", UserRole.USER, true));
        given(userAccountService.getUserPrincipal("jooUser@gmail.com"))
                .willReturn(BoardPrincipal.from(FixtureDto.getUserAccountDto("jooUser", UserRole.USER, true)));
        given(userAccountService.searchUser("notSignedJooUser@gmail.com"))
                .willReturn(FixtureDto.getUserAccountDto("notSignedJooUser", UserRole.USER, false));
        given(userAccountService.searchUser("jooAdmin@gmail.com"))
                .willReturn(FixtureDto.getUserAccountDto("jooAdmin", UserRole.ADMIN, true));
        willDoNothing().given(userAccountService).saveUser(any());

        given(tokenProvider.generateRefreshToken()).willReturn("refreshToken");
    }
}
