package joo.project.my3d.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.security.CustomOAuth2SuccessHandler;
import joo.project.my3d.security.SecurityConfig;
import joo.project.my3d.security.TokenProvider;
import joo.project.my3d.service.impl.UserAccountService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@Import(SecurityConfig.class)
public class TestSecurityConfig {
    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private UserAccountService userAccountService;

    @MockBean
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @BeforeTestMethod
    public void securitySetUp() {
        //        given(userAccountService.searchUser("jooCompany@gmail.com"))
        //                .willReturn(FixtureDto.getUserAccountDto("jooCompany", UserRole.COMPANY));
        //        given(userAccountService.searchUser("jooUser@gmail.com"))
        //                .willReturn(FixtureDto.getUserAccountDto("jooUser", UserRole.USER));

        //        given(userAccountService.searchUser("notSignedJooUser@gmail.com"))
        //                .willReturn(FixtureDto.getUserAccountDto("notSignedJooUser", UserRole.USER));
        //        given(userAccountService.searchUser("jooAdmin@gmail.com"))
        //                .willReturn(FixtureDto.getUserAccountDto("jooAdmin", UserRole.ADMIN));

        given(userAccountService.getUserPrincipal(eq("jooCompany@gmail.com")))
                .willReturn(BoardPrincipal.from(FixtureDto.getUserAccountDto("jooCompany", UserRole.COMPANY)));
        Claims companyClaims = createClaims("jooCompany@gmail.com", "jooCompany", "1:COMPANY");
        given(tokenProvider.parseOrValidateClaims(eq("companyToken"))).willReturn(companyClaims);
        given(tokenProvider.getUserDetails(eq(companyClaims)))
                .willReturn(FixtureDto.getPrincipal("jooCompany", UserRole.COMPANY));

        given(userAccountService.getUserPrincipal(eq("jooUser@gmail.com")))
                .willReturn(BoardPrincipal.from(FixtureDto.getUserAccountDto("jooUser", UserRole.USER)));
        Claims userClaims = createClaims("jooUser@gmail.com", "jooUser", "1:USER");
        given(tokenProvider.parseOrValidateClaims(eq("userToken"))).willReturn(userClaims);
        given(tokenProvider.getUserDetails(eq(userClaims)))
                .willReturn(FixtureDto.getPrincipal("jooUser", UserRole.USER));

        given(userAccountService.getUserPrincipal(eq("anonymous@gmail.com")))
                .willReturn(BoardPrincipal.from(FixtureDto.getUserAccountDto("anonymous", UserRole.ANONYMOUS)));
        Claims anonymousClaims = createClaims("anonymous@gmail.com", "anonymous", "1:ANONYMOUS");
        given(tokenProvider.parseOrValidateClaims(eq("anonymousToken"))).willReturn(anonymousClaims);
        given(tokenProvider.getUserDetails(eq(anonymousClaims)))
                .willReturn(FixtureDto.getPrincipal("anonymous", UserRole.ANONYMOUS));
    }

    private Claims createClaims(String email, String nickname, String spec) {
        Claims claims = Jwts.claims();
        claims.put("email", email);
        claims.put("nickname", nickname);
        claims.put("spec", spec);
        return claims;
    }
}
