package joo.project.my3d.api;

import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.request.SignUpRequest;
import joo.project.my3d.dto.response.LoginResponse;
import joo.project.my3d.fixture.FixtureCookie;
import joo.project.my3d.security.TokenProvider;
import joo.project.my3d.service.CompanyServiceInterface;
import joo.project.my3d.service.UserAccountServiceInterface;
import joo.project.my3d.util.FormDataEncoder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@ActiveProfiles("test")
@DisplayName("View 컨트롤러 - 로그인")
@Import({TestSecurityConfig.class, FormDataEncoder.class})
@WebMvcTest(SignUpApi.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class SignUpApiTest {
    @Autowired private MockMvc mvc;
    @Autowired private FormDataEncoder formDataEncoder;
    @Autowired private UserAccountServiceInterface userAccountService;
    @Autowired private TokenProvider tokenProvider;
    @MockBean private CompanyServiceInterface companyService;
    @MockBean private BCryptPasswordEncoder passwordEncoder;
    private static Cookie anonymousCookie = FixtureCookie.createAnonymousCookie();

    @Order(0)
    @DisplayName("[POST] 회원가입 요청 - 개인 사용자")
    @Test
    void requestSignup() throws Exception {
        // Given
        String email = "tester@gmail.com";
        SignUpRequest request = new SignUpRequest(email, UserRole.USER, null, "tester", "pw1234@@", "1234", "address", "detailAddress");
        given(userAccountService.isExistsUserEmailOrNickname(anyString(), anyString())).willReturn(false);
        given(tokenProvider.generateRefreshToken()).willReturn("refreshToken");
        willDoNothing().given(userAccountService).saveUser(any(UserAccount.class));
        given(userAccountService.login(anyString(), anyString()))
                .willReturn(LoginResponse.of("accessToken", "refreshToken"));
        // When
        mvc.perform(
                        post("/api/v1/signup")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(request))
                                .cookie(anonymousCookie)
                                .with(csrf())
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
        // Then
    }

    @Order(1)
    @DisplayName("[POST][Validation Error] 회원가입 요청 - 닉네임 미입력")
    @Test
    void requestSignup_validationError() throws Exception {
        // Given
        String email = "tester@gmail.com";
        SignUpRequest request = new SignUpRequest(email, UserRole.USER, null, "", "pw1234@@", "1234", "address", "detailAddress");
        given(tokenProvider.generateRefreshToken()).willReturn("refreshToken");
        willDoNothing().given(userAccountService).saveUser(any(UserAccount.class));
        given(userAccountService.login(anyString(), anyString()))
                .willReturn(LoginResponse.of("accessToken", "refreshToken"));
        // When
        mvc.perform(
                        post("/api/v1/signup")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(request))
                                .cookie(anonymousCookie)
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.errors.size()").value(1));
        // Then
    }
}