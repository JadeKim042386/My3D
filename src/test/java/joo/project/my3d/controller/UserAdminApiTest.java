package joo.project.my3d.controller;

import joo.project.my3d.api.UserAdminApi;
import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.domain.Company;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.CompanyDto;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.dto.properties.JwtProperties;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureCookie;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.security.CustomOAuth2SuccessHandler;
import joo.project.my3d.security.SecurityConfig;
import joo.project.my3d.security.TokenProvider;
import joo.project.my3d.service.CompanyServiceInterface;
import joo.project.my3d.service.impl.AlarmService;
import joo.project.my3d.service.impl.CompanyService;
import joo.project.my3d.service.impl.UserAccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@DisplayName("View 컨트롤러 - 유저 정보 관리")
@Import(TestSecurityConfig.class)
@WebMvcTest(UserAdminApi.class)
class UserAdminApiTest {
    @Autowired private MockMvc mvc;
    @Autowired private UserAccountService userAccountService;
    @MockBean private CompanyServiceInterface companyService;
    private static Cookie userCookie = FixtureCookie.createUserCookie();
    private static Cookie companyCookie = FixtureCookie.createCompanyCookie();

    @DisplayName("[POST] 사용자 정보 수정 요청")
    @Test
    void updateUserData() throws Exception {
        //given
        willDoNothing().given(userAccountService).updateUser(any(UserAccountDto.class));
        //when
        mvc.perform(
                        put("/api/v1/admin")
                                .param("nickname", "nickname")
                                .param("password", "pw")
                                .param("phone", "01011111111")
                                .param("email", "example@gmail.com")
                                .param("detail", "123")
                                .param("street", "강원특별자치도")
                                .param("zipcode", "12345")
                                .cookie(userCookie)
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
        ;
        //then
    }

    @DisplayName("[POST] 비밀번호 변경 요청")
    @Test
    void changePassword() throws Exception {
        //given
        willDoNothing().given(userAccountService).changePassword(anyString(), anyString());
        //when
        mvc.perform(
                put("/api/v1/admin/password")
                        .cookie(userCookie)
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty());
        //then
    }

    @DisplayName("[POST] 기업 정보 수정 요청")
    @Test
    void updateCompany() throws Exception {
        // Given
        given(companyService.updateCompany(any(), anyString())).willReturn(FixtureDto.getCompanyDto());
        // When
        mvc.perform(
                put("/api/v1/admin/company")
                        .cookie(companyCookie)
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.companyName").value("company"));
        // Then
    }


}