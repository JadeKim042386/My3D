package joo.project.my3d.controller;

import joo.project.my3d.api.SignInApi;
import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.request.SignUpRequest;
import joo.project.my3d.dto.response.LoginResponse;
import joo.project.my3d.fixture.FixtureCookie;
import joo.project.my3d.service.UserAccountServiceInterface;
import joo.project.my3d.service.impl.CompanyService;
import joo.project.my3d.service.impl.UserAccountService;
import joo.project.my3d.util.FormDataEncoder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@ActiveProfiles("test")
@DisplayName("View 컨트롤러 - 로그인")
@Import(TestSecurityConfig.class)
@WebMvcTest(SignInApi.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class SignInApiTest {

    @Autowired private MockMvc mvc;
    @Autowired private UserAccountServiceInterface userAccountService;
    private static Cookie anonymousCookie = FixtureCookie.createAnonymousCookie();
    private static Cookie userCookie = FixtureCookie.createUserCookie();

    @Order(0)
    @DisplayName("[POST] 로그인 요청")
    @Test
    void requestLogin() throws Exception {
        //given
        given(userAccountService.login(anyString(), anyString()))
                .willReturn(LoginResponse.of("accessToken", "refreshToken"));
        //when
        mvc.perform(
                post("/api/v1/signin")
                        .param("email", "a@gmail.com")
                        .param("password", "pw")
                        .cookie(anonymousCookie)
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
        //then
    }

    @Order(1)
    @DisplayName("[GET] OAuth 로그인 후 응답 API")
    @Test
    void oauthResponse() throws Exception {
        //given
        given(userAccountService.oauthLogin(anyString(), anyString()))
                .willReturn(LoginResponse.of("accessToken", "refreshToken"));
        //when
        mvc.perform(
                get("/api/v1/signin/oauth")
                        .param("email", "a@gmail.com")
                        .param("nickname", "nickname")
                        .param("signup", "true")
                        .cookie(anonymousCookie)
            )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
        //then
    }

    @Order(2)
    @DisplayName("[GET] 토큰을 통해 userRole 확인")
    @Test
    void parseSpecificationFromToken() throws Exception {
        //given
        //when
        mvc.perform(
                get("/api/v1/signin/info")
                        .cookie(userCookie)

        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("jooUser@gmail.com"))
                .andExpect(jsonPath("$.nickname").value("jooUser"))
                .andExpect(jsonPath("$.userRole").value("USER"));
        //then
    }
}
