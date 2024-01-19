package joo.project.my3d.controller;

import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.service.EmailService;
import joo.project.my3d.service.UserAccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("View 컨트롤러 - 이메일")
@Import(TestSecurityConfig.class)
@WebMvcTest(EmailController.class)
class EmailControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private UserAccountService userAccountService;
    @MockBean private EmailService emailService;

    @DisplayName("[POST] 이메일 인증 발송 - 정상 발송")
    @Test
    void sendEmail() throws Exception {
        // Given
        String email = "tester@gmail.com";
        String subject = "[My3D] 이메일 인증";
        given(userAccountService.searchUser(email)).willReturn(FixtureDto.getUserAccountDto());
        willDoNothing().given(emailService).sendEmail(eq(email), eq(subject), anyString());
        // When
        mvc.perform(
                        post("/mail/send_code")
                                .queryParam("email", email)
                                .queryParam("userRole", String.valueOf(UserRole.USER))
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
        // Then
    }

    @DisplayName("[POST] 이메일 인증 발송 - 이미 존재하는 이메일")
    @Test
    void sendEmailDuplicatedError() throws Exception {
        // Given
        String email = "jk042386@gmail.com";
        given(userAccountService.isExistsUserEmail(anyString())).willReturn(true);
        // When
        mvc.perform(
                        post("/mail/send_code")
                                .queryParam("email", email)
                                .queryParam("userRole", String.valueOf(UserRole.USER))
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
        // Then
    }

    @DisplayName("[POST] 이메일 인증 발송 - 잘못된 이메일 형식")
    @Test
    void sendEmail_invalidEmail() throws Exception {
        // Given
        String email = "jk042386gmail.com";
        // When
        mvc.perform(
                        post("/mail/send_code")
                                .queryParam("email", email)
                                .queryParam("userRole", String.valueOf(UserRole.USER))
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").isNotEmpty());
        // Then
    }

    @DisplayName("[POST] 임시 비밀번호 발송 - 정상 발송")
    @Test
    void sendEmailFindPass() throws Exception {
        // Given
        String email = "jk042386@gmail.com";
        given(userAccountService.isExistsUserEmail(anyString())).willReturn(true);
        willDoNothing().given(emailService).sendEmail(eq(email), eq("[My3D] 이메일 임시 비밀번호"), anyString());
        willDoNothing().given(userAccountService).changePassword(anyString(), anyString());
        // When
        mvc.perform(
                post("/mail/find_pass")
                        .queryParam("email", email)
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));
        // Then
    }

    @DisplayName("[POST] 임시 비밀번호 발송 - 존재하지 않는 계정")
    @Test
    void sendEmailFindPassNotFound() throws Exception {
        // Given
        String email = "tester@gmail.com";
        given(userAccountService.isExistsUserEmail(anyString())).willReturn(false);
        // Whe
        mvc.perform(
                        post("/mail/find_pass")
                                .queryParam("email", email)
                                .with(csrf())
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").isNotEmpty());
        // Then
    }
}
