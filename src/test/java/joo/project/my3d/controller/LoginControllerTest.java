package joo.project.my3d.controller;

import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.request.SignUpRequest;
import joo.project.my3d.service.SignUpService;
import joo.project.my3d.service.UserAccountService;
import joo.project.my3d.util.FormDataEncoder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러 - 로그인과 회원가입")
@Import({TestSecurityConfig.class, FormDataEncoder.class})
@WebMvcTest(LoginController.class)
class LoginControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private FormDataEncoder formDataEncoder;
    @Autowired private UserAccountService userAccountService;
    @MockBean private SignUpService signUpService;

    @DisplayName("[GET] 로그인 페이지")
    @Test
    void login() throws Exception {
        // Given

        // When
        mvc.perform(get("/account/login"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("account/login"));
        // Then
    }

    @DisplayName("[GET] 로그아웃 페이지")
    @WithMockUser
    @Test
    void logout() throws Exception {
        // Given

        // When
        mvc.perform(get("/account/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
        // Then
    }

    @DisplayName("[GET] 회원 유형 선택 페이지 - 회원가입되지 않은 유저")
    @Test
    void selectTypeByNotSignedUser() throws Exception {
        // Given

        // When
        mvc.perform(
                    get("/account/type")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("account/type"));
        // Then
    }

    @DisplayName("[GET] 회원 유형 선택 페이지 - 회원가입된 유저")
    @Test
    @WithUserDetails(value = "jooUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    void selectTypeBySignedUser() throws Exception {
        // Given

        // When
        mvc.perform(
                        get("/account/type")
                )
                .andExpect(status().isForbidden());
        // Then
    }

    @DisplayName("[GET] 회원가입 페이지 - OAuth 로그인 없이 회원 유형 선택 후")
    @Test
    void signupNotOauthAfterSelectedType() throws Exception {
        // Given

        // When
        mvc.perform(
                    get("/account/sign_up")
                            .queryParam("userRole", String.valueOf(UserRole.USER))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("account/signup"))
                .andExpect(model().attributeDoesNotExist("oauthLogin"))
                .andExpect(model().attributeDoesNotExist("email"))
                .andExpect(model().attributeDoesNotExist("code"))
                .andExpect(model().attributeDoesNotExist("emailError"))
                .andExpect(model().attributeExists("signUpData"))
                .andExpect(model().attributeExists("nicknames"))
                .andExpect(model().attributeExists("companyNames"));
        // Then
    }
    @DisplayName("[GET] 회원가입 페이지 - OAuth 로그인하고 회원 유형 선택 후")
    @Test
    void signupOauthAfterSelectedType() throws Exception {
        // Given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("oauthLogin", true);
        session.setAttribute("email", "tester@gmail.com");
        session.setAttribute("nickname", "tester");
        // When
        mvc.perform(
                        get("/account/sign_up")
                                .queryParam("userRole", String.valueOf(UserRole.USER))
                                .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("account/signup"))
                .andExpect(model().attributeExists("oauthLogin"))
                .andExpect(model().attributeExists("email"))
                .andExpect(model().attributeDoesNotExist("code"))
                .andExpect(model().attributeDoesNotExist("emailError"))
                .andExpect(model().attributeExists("signUpData"))
                .andExpect(model().attributeExists("nicknames"))
                .andExpect(model().attributeExists("companyNames"));
        // Then
    }

    @DisplayName("[GET] 회원가입 페이지 - 메일 전송 후 redirect (OAuth 로그인 여부 생략)")
    @Test
    void signupAfterRedirect() throws Exception {
        // Given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userRole", UserRole.USER);
        session.setAttribute("email", "tester@gmail.com");
        session.setAttribute("nickname", "tester");
        session.setAttribute("emailCode", 1234);
        // When
        mvc.perform(
                        get("/account/sign_up")
                                .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("account/signup"))
                .andExpect(model().attributeExists("email"))
                .andExpect(model().attributeExists("code"))
                .andExpect(model().attributeDoesNotExist("emailError"))
                .andExpect(model().attributeExists("signUpData"))
                .andExpect(model().attributeExists("nicknames"))
                .andExpect(model().attributeExists("companyNames"));
        // Then
    }

    @DisplayName("[GET] 회원가입 페이지 - 메일 전송 후 redirect (메일 중복)")
    @Test
    void signupAfterRedirectDuplicatedEmail() throws Exception {
        // Given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userRole", UserRole.USER);
        session.setAttribute("email", "tester@gmail.com");
        session.setAttribute("nickname", "tester");
        session.setAttribute("emailError", "duplicated");
        // When
        mvc.perform(
                        get("/account/sign_up")
                                .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("account/signup"))
                .andExpect(model().attributeExists("email"))
                .andExpect(model().attributeDoesNotExist("code"))
                .andExpect(model().attributeExists("emailError"))
                .andExpect(model().attributeExists("signUpData"))
                .andExpect(model().attributeExists("nicknames"))
                .andExpect(model().attributeExists("companyNames"));
        // Then
    }

    @DisplayName("[POST] 회원가입 요청 - 개인 사용자")
    @Test
    void requestSignup() throws Exception {
        // Given
        String email = "tester@gmail.com";
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("email", email);
        SignUpRequest request = new SignUpRequest(UserRole.USER, null, "tester", "pw1234@@", "1234", "address", "detailAddress");
        willDoNothing().given(userAccountService).saveUser(request.toEntity(email));
        // When
        mvc.perform(
                        post("/account/sign_up")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .content(formDataEncoder.encode(request))
                                .session(session)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
        // Then
        then(userAccountService).should().saveUser(request.toEntity(email));
    }

    @DisplayName("[GET] 비밀번호 찾기 페이지")
    @Test
    void findPassword() throws Exception {
        // Given

        // When
        mvc.perform(get("/account/find_pass"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("account/find-pass"));
        // Then
    }

    @DisplayName("[GET] 임시 비밀번호 전송 완료 페이지")
    @Test
    void findPasswordSuccess() throws Exception {
        // Given

        // When
        mvc.perform(get("/account/find_pass_success"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("account/find-pass-success"));
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
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("account/company"));
        // Then
    }

    @DisplayName("[GET] 사업자 인증 페이지 - 수정 요청")
    @Test
    void businessCertificationAfterModify() throws Exception {
        // Given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("b_no", "2208162517");
        // When
        mvc.perform(
                        get("/account/company")
                                .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("account/company"))
                .andExpect(model().attributeExists("certification"));
        // Then
    }

    @DisplayName("[GET] 사업자 인증 페이지 - 사업자 인증 후 redirect")
    @Test
    void businessCertificationAfterRedirect() throws Exception {
        // Given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("b_stt_cd", "01");
        session.setAttribute("b_no", "2208162517");
        // When
        mvc.perform(
                        get("/account/company")
                                .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("account/company"))
                .andExpect(model().attributeExists("certification"));
        // Then
    }

    @DisplayName("[POST] 사업자 인증 페이지 - 정상 인증")
    @Test
    void requestBusinessCertification() throws Exception {
        // Given
        given(signUpService.businessCertification(anyString())).willReturn(anyString());
        // When
        mvc.perform(
                    post("/account/company")
                            .queryParam("b_no", "2208162517")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/account/company"))
                .andExpect(redirectedUrl("/account/company"));
        // Then
        then(signUpService).should().businessCertification(anyString());
    }

    @DisplayName("[POST] 사업자 인증 페이지 - 사업자 등록번호 길이가 10이 아닌 경우")
    @Test
    void requestBusinessCertificationLengthError() throws Exception {
        // Given

        // When
        mvc.perform(
                        post("/account/company")
                                .queryParam("b_no", "22081627")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("/account/company"));
        // Then
        then(signUpService).shouldHaveNoInteractions();
    }
}
