package joo.project.my3d.controller;

import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.fixture.FixtureDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러 - 로그인과 회원가입")
@Import(TestSecurityConfig.class)
@WebMvcTest(LoginController.class)
class LoginControllerTest {

    @Autowired private MockMvc mvc;

    @DisplayName("[GET] 로그인 페이지")
    @WithMockUser
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

    @DisplayName("[GET] 회원 유형 선택 페이지")
    @WithUserDetails(value = "notSignedJooUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void selectType() throws Exception {
        // Given

        // When
        mvc.perform(
                    get("/account/type")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("account/type"))
                .andExpect(model().attributeExists("signUp"))
                .andExpect(model().attributeExists("email"))
                .andExpect(model().attributeExists("nickname"));
        // Then
    }

    @DisplayName("[GET] 회원가입 페이지")
    @WithMockUser
    @Test
    void signup() throws Exception {
        // Given

        // When
        mvc.perform(get("/account/sign_up"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("account/signup"));
        // Then
    }

    @DisplayName("[GET] 비밀번호 찾기 페이지")
    @WithMockUser
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
}