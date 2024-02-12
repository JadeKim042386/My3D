package joo.project.my3d.controller;

import joo.project.my3d.api.EmailApi;
import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.fixture.FixtureCookie;
import joo.project.my3d.service.impl.EmailService;
import joo.project.my3d.service.impl.UserAccountService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("View 컨트롤러 - 이메일")
@Import(TestSecurityConfig.class)
@WebMvcTest(EmailApi.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class EmailApiTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserAccountService userAccountService;

    @MockBean
    private EmailService emailService;

    private static Cookie anonymousCookie = FixtureCookie.createAnonymousCookie();

    @Order(0)
    @DisplayName("[POST] 이메일 인증 발송 - 정상 발송")
    @Test
    void sendEmail() throws Exception {
        // Given
        String email = "tester@gmail.com";
        String subject = "[My3D] 이메일 인증";
        given(userAccountService.isExistsUserEmailOrNickname(anyString())).willReturn(false);
        willDoNothing().given(emailService).sendEmail(eq(email), eq(subject), anyString());
        // When
        mvc.perform(post("/api/v1/mail/send_code")
                        .queryParam("email", email)
                        .cookie(anonymousCookie)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
        // Then
    }

    @Order(1)
    @DisplayName("[POST] 이메일 인증 발송 - 이미 존재하는 이메일")
    @Test
    void sendEmailDuplicatedError() throws Exception {
        // Given
        String email = "jk042386@gmail.com";
        given(userAccountService.isExistsUserEmailOrNickname(anyString())).willReturn(true);
        // When
        mvc.perform(post("/api/v1/mail/send_code")
                        .queryParam("email", email)
                        .cookie(anonymousCookie)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
        // Then
    }

    @Order(2)
    @DisplayName("[POST] 이메일 인증 발송 - 잘못된 이메일 형식")
    @Test
    void sendEmail_invalidEmail() throws Exception {
        // Given
        String email = "jk042386gmail.com";
        // When
        mvc.perform(post("/api/v1/mail/send_code")
                        .queryParam("email", email)
                        .cookie(anonymousCookie)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
        // Then
    }

    @Order(3)
    @DisplayName("[POST] 임시 비밀번호 발송 - 정상 발송")
    @Test
    void sendEmailFindPass() throws Exception {
        // Given
        String email = "jk042386@gmail.com";
        given(userAccountService.isExistsUserEmailOrNickname(anyString())).willReturn(true);
        willDoNothing().given(emailService).sendEmail(anyString(), anyString(), anyString());
        given(userAccountService.sendTemporaryPassword(anyString())).willReturn("abcde123");
        // When
        mvc.perform(post("/api/v1/mail/find_pass")
                        .queryParam("email", email)
                        .cookie(anonymousCookie)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isNotEmpty());
        // Then
    }

    @Order(4)
    @DisplayName("[POST] 임시 비밀번호 발송 - 존재하지 않는 계정")
    @Test
    void sendEmailFindPassNotFound() throws Exception {
        // Given
        String email = "tester@gmail.com";
        given(userAccountService.isExistsUserEmailOrNickname(anyString())).willReturn(false);
        // Whe
        mvc.perform(post("/api/v1/mail/find_pass")
                        .queryParam("email", email)
                        .cookie(anonymousCookie)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());
        // Then
    }
}
