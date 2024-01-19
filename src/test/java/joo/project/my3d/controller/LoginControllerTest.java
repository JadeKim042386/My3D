package joo.project.my3d.controller;

import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.request.SignUpRequest;
import joo.project.my3d.dto.response.LoginResponse;
import joo.project.my3d.service.CompanyService;
import joo.project.my3d.service.UserAccountService;
import joo.project.my3d.util.FormDataEncoder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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
    @MockBean private CompanyService companyService;
    @MockBean private SecurityContextLogoutHandler logoutHandler;

    @DisplayName("[POST] 로그인 요청")
    @Test
    void requestLogin() throws Exception {
        //given
        given(userAccountService.login(anyString(), anyString()))
                .willReturn(LoginResponse.of("a@gmail.com", "nickname", "accessToken", "refreshToken"));
        //when
        mvc.perform(
                post("/account/login")
                        .param("email", "a@gmail.com")
                        .param("password", "pw")
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("a@gmail.com"))
                .andExpect(jsonPath("$.nickname").value("nickname"))
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
        //then
    }

    @DisplayName("[GET] OAuth 로그인 후 응답 API")
    @Test
    void oauthResponse() throws Exception {
        //given
        given(userAccountService.oauthLogin(anyString(), anyString()))
                .willReturn(LoginResponse.of("a@gmail.com", "nickname", "accessToken", "refreshToken"));
        //when
        mvc.perform(
                get("/account/oauth/response")
                        .param("email", "a@gmail.com")
                        .param("nickname", "nickname")
                        .param("signup", "true")
            )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("a@gmail.com"))
                .andExpect(jsonPath("$.nickname").value("nickname"))
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
        //then
    }

    @DisplayName("[POST] 닉네임 중복 체크")
    @Test
    void duplicatedNicknamesCheck() throws Exception {
        //given
        given(userAccountService.isExistsUserNickname(anyString())).willReturn(true);
        //when
        mvc.perform(
                post("/account/signup/duplicatedCheck")
                        .queryParam("nickname", "nickname")
                        .with(csrf())
            )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duplicated").value(true));
        //then
    }

    @DisplayName("[POST] 기업명 중복 체크")
    @Test
    void duplicatedCompanyNamesCheck() throws Exception {
        //given
        given(companyService.isExistsByCompanyName(anyString())).willReturn(true);
        //when
        mvc.perform(
                        post("/account/signup/duplicatedCheck")
                                .queryParam("companyName", "company")
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duplicated").value(true));
        //then
    }

    @DisplayName("[POST] 회원가입 요청 - 개인 사용자")
    @Test
    void requestSignup() throws Exception {
        // Given
        String email = "tester@gmail.com";
        SignUpRequest request = new SignUpRequest(UserRole.USER, null, "tester", "pw1234@@", "1234", "address", "detailAddress");
        willDoNothing().given(userAccountService).saveUser(any(UserAccount.class));
        given(userAccountService.login(anyString(), anyString()))
                .willReturn(LoginResponse.of(email, "nickname", "accessToken", "refreshToken"));
        // When
        mvc.perform(
                        post("/account/signup")
                                .queryParam("email", email)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(request))
                                .with(csrf())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email));
        // Then
    }

    @DisplayName("[POST][Validation Error] 회원가입 요청 - 닉네임 미입력")
    @Test
    void requestSignup_validationError() throws Exception {
        // Given
        String email = "tester@gmail.com";
        SignUpRequest request = new SignUpRequest(UserRole.USER, null, "", "pw1234@@", "1234", "address", "detailAddress");
        willDoNothing().given(userAccountService).saveUser(any(UserAccount.class));
        given(userAccountService.login(anyString(), anyString()))
                .willReturn(LoginResponse.of(email, "nickname", "accessToken", "refreshToken"));
        // When
        mvc.perform(
                        post("/account/signup")
                                .queryParam("email", email)
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(request))
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.errors.size()").value(1));
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
                .andExpect(jsonPath("$").doesNotExist());
        // Then
    }
}
