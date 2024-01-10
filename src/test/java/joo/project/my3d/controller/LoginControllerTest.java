package joo.project.my3d.controller;

import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.request.SignUpRequest;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.service.SignUpService;
import joo.project.my3d.service.UserAccountService;
import joo.project.my3d.util.FormDataEncoder;
import joo.project.my3d.utils.CookieUtils;
import joo.project.my3d.utils.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@ActiveProfiles("test")
@DisplayName("View 컨트롤러 - 로그인과 회원가입")
@Import({TestSecurityConfig.class, FormDataEncoder.class})
@WebMvcTest(LoginController.class)
class LoginControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private FormDataEncoder formDataEncoder;
    @Autowired private UserAccountService userAccountService;
    @MockBean private SignUpService signUpService;
    @MockBean private SecurityContextLogoutHandler logoutHandler;

    @DisplayName("[GET] 로그인 페이지")
    @Test
    void getLogin() throws Exception {
        // Given

        // When
        mvc.perform(
                get("/account/login")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isEmpty());
        // Then
    }

    @DisplayName("[POST] 로그인 요청")
    @Test
    void requestLogin() throws Exception {
        //given
        given(userAccountService.login(anyString(), anyString())).willReturn("aa");
        //when
        mvc.perform(
                post("/account/login")
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isEmpty());
        //then
    }

    @DisplayName("[GET] 로그아웃 요청")
    @Test
    void requestLogout() throws Exception {
        //given
        willDoNothing().given(logoutHandler).logout(any(), any(), any());
        //when
        mvc.perform(
                        get("/account/logout")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isEmpty());
        //then
    }

    @DisplayName("[GET] 회원 유형 선택 페이지")
    @Test
    void selectTypeByNotSignedUser() throws Exception {
        // Given

        // When
        mvc.perform(
                    get("/account/type")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isEmpty());
        // Then
    }
    @DisplayName("[GET] 회원가입 페이지 - OAuth 로그인 없이 회원 유형 선택 후")
    @WithAnonymousUser
    @Test
    void signupNotOauthAfterSelectedType() throws Exception {
        // Given
        given(userAccountService.findAllUser()).willReturn(List.of(FixtureDto.getUserAccountDto()));
        // When
        mvc.perform(
                    get("/account/signup")
                            .queryParam("userRole", String.valueOf(UserRole.USER))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.oAuthLogin").value(false))
                .andExpect(jsonPath("$.data.email").isEmpty())
                .andExpect(jsonPath("$.data.emailCode").isEmpty())
                .andExpect(jsonPath("$.data.emailError").isEmpty())
                .andExpect(jsonPath("$.data.userRole").value(UserRole.USER.toString()))
                .andExpect(jsonPath("$.data.nickname").isEmpty())
                .andExpect(jsonPath("$.data.nicknames.size()").value(1))
                .andExpect(jsonPath("$.data.companyNames.size()").value(0))
                .andExpect(jsonPath("$.data.validError").isEmpty())
                ;
        // Then
    }
    @DisplayName("[GET] 회원가입 페이지 - OAuth 로그인하고 회원 유형 선택 후")
    @Test
    void signupOauthAfterSelectedType() throws Exception {
        // Given
        Cookie cookie = CookieUtils.createCookie(
                "token",
                JwtTokenUtils.generateToken(
                        "b@gmail.com",
                        "B",
                        "aaaaagaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                        100000L
                ),
                100,
                "/"
        );
        given(userAccountService.findAllUser()).willReturn(List.of(FixtureDto.getUserAccountDto()));
        // When
        mvc.perform(
                        get("/account/signup")
                                .queryParam("userRole", String.valueOf(UserRole.USER))
                                .cookie(cookie)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.oAuthLogin").value(true))
                .andExpect(jsonPath("$.data.email").value("b@gmail.com"))
                .andExpect(jsonPath("$.data.emailCode").isEmpty())
                .andExpect(jsonPath("$.data.emailError").isEmpty())
                .andExpect(jsonPath("$.data.userRole").value(UserRole.USER.toString()))
                .andExpect(jsonPath("$.data.nickname").value("B"))
                .andExpect(jsonPath("$.data.nicknames.size()").value(1))
                .andExpect(jsonPath("$.data.companyNames.size()").value(0))
                .andExpect(jsonPath("$.data.validError").isEmpty())
        ;
        // Then
    }

    @DisplayName("[GET] 회원가입 페이지 - 메일 전송 후 redirect (OAuth 로그인 여부 생략)")
    @Test
    void signupAfterRedirect() throws Exception {
        // Given
        given(userAccountService.findAllUser()).willReturn(List.of(FixtureDto.getUserAccountDto()));
        // When
        mvc.perform(
                        get("/account/signup")
                                .queryParam("userRole", String.valueOf(UserRole.USER))
                                .queryParam("email", "b@gmail.com")
                                .queryParam("emailCode", "01")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.oAuthLogin").value(false))
                .andExpect(jsonPath("$.data.email").value("b@gmail.com"))
                .andExpect(jsonPath("$.data.emailCode").value("01"))
                .andExpect(jsonPath("$.data.emailError").isEmpty())
                .andExpect(jsonPath("$.data.userRole").value(UserRole.USER.toString()))
                .andExpect(jsonPath("$.data.nickname").isEmpty())
                .andExpect(jsonPath("$.data.nicknames.size()").value(1))
                .andExpect(jsonPath("$.data.companyNames.size()").value(0))
                .andExpect(jsonPath("$.data.validError").isEmpty())
        ;
        // Then
    }

    @DisplayName("[GET] 회원가입 페이지 - 메일 전송 후 redirect (메일 중복)")
    @Test
    void signupAfterRedirectDuplicatedEmail() throws Exception {
        // Given
        given(userAccountService.findAllUser()).willReturn(List.of(FixtureDto.getUserAccountDto()));
        // When
        mvc.perform(
                        get("/account/signup")
                                .queryParam("userRole", String.valueOf(UserRole.USER))
                                .queryParam("email", "b@gmail.com")
                                .queryParam("emailError", "duplicated")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.oAuthLogin").value(false))
                .andExpect(jsonPath("$.data.email").value("b@gmail.com"))
                .andExpect(jsonPath("$.data.emailCode").isEmpty())
                .andExpect(jsonPath("$.data.emailError").value("duplicated"))
                .andExpect(jsonPath("$.data.userRole").value(UserRole.USER.toString()))
                .andExpect(jsonPath("$.data.nickname").isEmpty())
                .andExpect(jsonPath("$.data.nicknames.size()").value(1))
                .andExpect(jsonPath("$.data.companyNames.size()").value(0))
                .andExpect(jsonPath("$.data.validError").isEmpty())
        ;
        // Then
    }

    @DisplayName("[POST] 회원가입 요청 - 개인 사용자")
    @Test
    void requestSignup() throws Exception {
        // Given
        String email = "tester@gmail.com";
        SignUpRequest request = new SignUpRequest(UserRole.USER, null, "tester", "pw1234@@", "1234", "address", "detailAddress");
        willDoNothing().given(signUpService).setPrincipal(any());
        willDoNothing().given(userAccountService).saveUser(any(UserAccount.class));
        // When
        mvc.perform(
                        post("/account/signup")
                                .queryParam("email", email)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(request))
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
        // Then
    }

    @DisplayName("[GET] 비밀번호 찾기 페이지")
    @Test
    void findPassword() throws Exception {
        // Given

        // When
        mvc.perform(
                get("/account/find_pass")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.email").isEmpty())
                .andExpect(jsonPath("$.data.emailError").isEmpty());
        // Then
    }

    @DisplayName("[GET] 임시 비밀번호 전송 완료 페이지")
    @Test
    void findPasswordSuccess() throws Exception {
        // Given

        // When
        mvc.perform(get("/account/find_pass_success"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isEmpty());
        // Then
    }

    @DisplayName("[GET] 사업자 인증 페이지 - 초기 페이지")
    @Test
    void businessCertification() throws Exception {
        // Given

        // When
        mvc.perform(
                    get("/account/company")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.b_no").isEmpty())
                .andExpect(jsonPath("$.data.b_stt_cd").isEmpty())
                .andExpect(jsonPath("$.data.serviceKey").isNotEmpty());
        // Then
    }

    @DisplayName("[GET] 사업자 인증 페이지 - 수정 요청")
    @Test
    void businessCertificationAfterModify() throws Exception {
        // Given

        // When
        mvc.perform(
                        get("/account/company")
                            .queryParam("b_no", "2208162517")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.b_no").value("2208162517"))
                .andExpect(jsonPath("$.data.b_stt_cd").isEmpty())
                .andExpect(jsonPath("$.data.serviceKey").isNotEmpty());
        // Then
    }

    @DisplayName("[GET] 사업자 인증 페이지 - 사업자 인증 후 redirect")
    @Test
    void businessCertificationAfterRedirect() throws Exception {
        // Given

        // When
        mvc.perform(
                        get("/account/company")
                            .queryParam("b_no", "2208162517")
                            .queryParam("b_stt_cd", "01")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.b_no").value("2208162517"))
                .andExpect(jsonPath("$.data.b_stt_cd").value("01"))
                .andExpect(jsonPath("$.data.serviceKey").isNotEmpty());
        // Then
    }
}
