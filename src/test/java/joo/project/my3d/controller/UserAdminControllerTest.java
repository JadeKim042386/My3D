package joo.project.my3d.controller;

import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.CompanyDto;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.dto.properties.JwtProperties;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.security.CustomOAuth2SuccessHandler;
import joo.project.my3d.security.SecurityConfig;
import joo.project.my3d.security.TokenProvider;
import joo.project.my3d.service.AlarmService;
import joo.project.my3d.service.CompanyService;
import joo.project.my3d.service.UserAccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@DisplayName("View 컨트롤러 - 유저 정보 관리")
@Import(SecurityConfig.class)
@EnableConfigurationProperties(value = JwtProperties.class)
@WebMvcTest(UserAdminController.class)
class UserAdminControllerTest {
    @Autowired private MockMvc mvc;
    @MockBean private UserAccountService userAccountService;
    @MockBean private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    @MockBean private CompanyService companyService;
    @MockBean private AlarmService alarmService;
    @MockBean private TokenProvider tokenProvider;

    @DisplayName("[GET] 사용자 정보 요청")
    @Test
    void requestUserData() throws Exception {
        //given
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("userUser", UserRole.USER);
        //when
        mvc.perform(
                get("/user/account")
                        .with(authentication(authentication))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.nickname").value("userUser"))
                .andExpect(jsonPath("$.data.email").value("userUser@gmail.com"));
        //then
    }

    @DisplayName("[POST] 사용자 정보 수정 요청")
    @Test
    void updateUserData() throws Exception {
        //given
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("userUser", UserRole.USER);
        willDoNothing().given(userAccountService).updateUser(any(UserAccountDto.class));
        //when
        mvc.perform(
                        post("/user/account")
                                .param("nickname", "nickname")
                                .param("password", "pw")
                                .param("phone", "01011111111")
                                .param("email", "example@gmail.com")
                                .param("detail", "123")
                                .param("street", "강원특별자치도")
                                .param("zipcode", "12345")
                                .with(authentication(authentication))
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isEmpty())
        ;
        //then
    }

    @DisplayName("[POST] 비밀번호 변경 요청")
    @Test
    void changePassword() throws Exception {
        //given
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("userUser", UserRole.USER);
        willDoNothing().given(userAccountService).changePassword(anyString(), anyString());
        //when
        mvc.perform(
                post("/user/password")
                        .with(authentication(authentication))
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isEmpty());
        //then
    }

    @DisplayName("[GET] 기업 정보 관리 페이지")
    @Test
    void company() throws Exception {
        // Given
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("userCompany", UserRole.COMPANY);
        CompanyDto companyDto = FixtureDto.getCompanyDto();
        given(userAccountService.getCompany(anyString())).willReturn(companyDto);
        // When
        mvc.perform(
                get("/user/company")
                        .with(authentication(authentication))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.companyName").value(companyDto.companyName()))
                .andExpect(jsonPath("$.data.homepage").value(companyDto.homepage()));
        // Then
    }

    @DisplayName("[POST] 기업 정보 수정 요청")
    @Test
    void updateCompany() throws Exception {
        // Given
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("userCompany", UserRole.COMPANY);
        given(userAccountService.getCompany(anyString())).willReturn(FixtureDto.getCompanyDto());
        willDoNothing().given(companyService).updateCompany(any(CompanyDto.class));
        // When
        mvc.perform(
                post("/user/company")
                        .with(authentication(authentication))
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isEmpty());
        // Then
    }

    @DisplayName("[GET] 알람 리스트 조회")
    @Test
    void getAlarms() throws Exception {
        // Given
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("jujoo042386", UserRole.COMPANY);
        given(userAccountService.getAlarms(anyString())).willReturn(List.of());
        // When
        mvc.perform(
                get("/user/alarm")
                        .with(authentication(authentication))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.size()").value(0))
        ;
        // Then
    }

    @DisplayName("[GET] SSE 연결")
    @Test
    void subscribe() throws Exception {
        // Given
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("jujoo042386", UserRole.COMPANY);
        given(alarmService.connectAlarm(anyString())).willReturn(null);
        // When
        mvc.perform(
                get("/user/alarm/subscribe")
                        .with(authentication(authentication))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").isEmpty())
        ;
        // Then
    }
}
