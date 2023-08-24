package joo.project.my3d.controller;

import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.service.EmailService;
import joo.project.my3d.service.UserAccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러 - 로그인과 회원가입")
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
        given(userAccountService.searchUser(email)).willReturn(Optional.empty());
        willDoNothing().given(emailService).sendEmail(eq(email), eq(subject), anyString());
        // When
        mvc.perform(
                        post("/mail/send_code")
                                .queryParam("email", email)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/account/sign_up"))
                .andExpect(redirectedUrl("/account/sign_up"));
        // Then
        then(userAccountService).should().searchUser(email);
        then(emailService).should().sendEmail(eq(email), eq(subject), anyString());
    }

    @DisplayName("[POST] 이메일 인증 발송 - 이미 존재하는 이메일")
    @Test
    void sendEmailDuplicatedError() throws Exception {
        // Given
        String email = "jk042386@gmail.com";
        UserAccountDto userAccountDto = FixtureDto.getUserAccountDto();
        given(userAccountService.searchUser(email)).willReturn(Optional.of(userAccountDto));
        // When
        mvc.perform(
                        post("/mail/send_code")
                                .queryParam("email", email)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/account/sign_up"))
                .andExpect(redirectedUrl("/account/sign_up"));
        // Then
        then(userAccountService).should().searchUser(email);
        then(emailService).shouldHaveNoInteractions();
    }

    @DisplayName("[POST] 임시 비밀번호 발송 - 정상 발송")
    @Test
    void sendEmailFindPass() throws Exception {
        // Given
        String email = "jk042386@gmail.com";
        given(userAccountService.searchUser(anyString())).willReturn(Optional.of(FixtureDto.getUserAccountDto()));
        willDoNothing().given(emailService).sendEmail(eq(email), eq("[My3D] 이메일 임시 비밀번호"), anyString());
        given(userAccountService.searchUser(anyString())).willReturn(Optional.of(FixtureDto.getUserAccountDto("admin", UserRole.ADMIN, true)));
        willDoNothing().given(userAccountService).changePassword(anyString(), anyString());
        // When
        mvc.perform(
                post("/mail/find_pass")
                        .queryParam("email", email)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/account/find_pass_success"))
                .andExpect(redirectedUrl("/account/find_pass_success"));
        // Then
        then(userAccountService).should(times(2)).searchUser(anyString());
        then(emailService).should().sendEmail(eq(email), eq("[My3D] 이메일 임시 비밀번호"), anyString());
        then(userAccountService).should().changePassword(anyString(), anyString());
    }

    @DisplayName("[POST] 임시 비밀번호 발송 - 존재하지 않는 계정")
    @Test
    void sendEmailFindPassNotFound() throws Exception {
        // Given
        String email = "tester@gmail.com";
        given(userAccountService.searchUser(anyString())).willReturn(Optional.empty());
        // Whe
        mvc.perform(
                        post("/mail/find_pass")
                                .queryParam("email", email)
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/account/find_pass"))
                .andExpect(redirectedUrl("/account/find_pass"));
        // Then
        then(userAccountService).should(times(1)).searchUser(anyString());
        then(emailService).shouldHaveNoInteractions();
    }
}
