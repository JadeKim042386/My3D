package joo.project.my3d.controller;

import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.service.SignUpService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러 - 로그인과 회원가입")
@Import(TestSecurityConfig.class)
@WebMvcTest(LoginController.class)
class LoginControllerTest {

    @Autowired private MockMvc mvc;
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

    @DisplayName("[GET] 회원 유형 선택 페이지")
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

    @DisplayName("[GET] 회원가입 페이지")
    @Test
    void signup() throws Exception {
        // Given
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("b_stt_cd", "01");
        // When
        mvc.perform(
                    get("/account/sign_up")
                            .session(session)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("account/signup"));
        // Then
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

    @DisplayName("[GET] 사업자 인증 페이지")
    @Test
    void businessCertification() throws Exception {
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
                .andExpect(model().attributeExists("b_stt_cd"))
                .andExpect(model().attributeExists("b_no"));
        // Then
    }

    @DisplayName("[POST] 사업자 인증 페이지")
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
}
