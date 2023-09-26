package joo.project.my3d.controller;

import joo.project.my3d.config.SecurityConfig;
import joo.project.my3d.config.handler.CustomOAuth2SuccessHandler;
import joo.project.my3d.domain.constant.OrderStatus;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.CompanyDto;
import joo.project.my3d.dto.OrdersDto;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.dto.properties.JwtProperties;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.service.AlarmService;
import joo.project.my3d.service.CompanyService;
import joo.project.my3d.service.OrdersService;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러 - 유저 정보 관리")
@Import(SecurityConfig.class)
@EnableConfigurationProperties(value = JwtProperties.class)
@WebMvcTest(UserAdminController.class)
class UserAdminControllerTest {
    @Autowired private MockMvc mvc;
    @MockBean private UserAccountService userAccountService;
    @MockBean private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;
    @MockBean private OrdersService ordersService;
    @MockBean private CompanyService companyService;
    @MockBean private AlarmService alarmService;

    @DisplayName("[GET] 계정 관리 페이지")
    @Test
    void userDataPage() throws Exception {
        // Given
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("userUser", UserRole.USER);
        // When
        mvc.perform(
                get("/user/account")
                        .with(authentication(authentication))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("user/account"))
                .andExpect(model().attributeExists("userData"));
        // Then
    }

    @DisplayName("[POST] 계정 정보 수정")
    @WithMockUser
    @Test
    void updateUserData() throws Exception {
        // Given
        willDoNothing().given(userAccountService).updateUser(any(UserAccountDto.class));
        // When
        mvc.perform(
                post("/user/account")
                        .param("userRole", "USER")
                        .param("nickname", "nickname")
                        .param("password", "pw")
                        .param("phone", "01011111111")
                        .param("email", "example@gmail.com")
                        .param("detail", "123")
                        .param("street", "강원특별자치도")
                        .param("zipcode", "12345")
                        .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user/account"))
                .andExpect(redirectedUrl("/user/account"));
        // Then
        then(userAccountService).should().updateUser(any(UserAccountDto.class));
    }

    @DisplayName("[GET] 주문 관리 페이지")
    @Test
    void orders() throws Exception {
        // Given
        given(ordersService.getOrdersByEmail(anyString())).willReturn(anyList());
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("userUser", UserRole.USER);
        // When
        mvc.perform(
                        get("/user/orders")
                                .with(authentication(authentication))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("user/orders"))
                .andExpect(model().attributeExists("orders"));
        // Then
        then(ordersService).should().getOrdersByEmail(anyString());
    }

    @DisplayName("[POST] 주문 상태 변경 요청")
    @Test
    void updateOrdersStatus() throws Exception {
        // Given
        willDoNothing().given(ordersService).updateOrders(any(OrdersDto.class));
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("userCompany", UserRole.COMPANY);
        // When
        mvc.perform(
                        post("/user/orders")
                                .param("id", String.valueOf(1L))
                                .param("productName", "example product")
                                .param("zipcode", "zipcode")
                                .param("detail", "detail")
                                .param("street", "street")
                                .param("status", OrderStatus.REQUEST.name())
                                .with(authentication(authentication))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user/orders"))
                .andExpect(redirectedUrl("/user/orders"));
        // Then
        then(ordersService).should().updateOrders(any(OrdersDto.class));
    }

    @DisplayName("[GET] 기업 정보 관리 페이지")
    @Test
    void company() throws Exception {
        // Given
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("userCompany", UserRole.COMPANY);
        given(userAccountService.getCompany("userCompany@gmail.com")).willReturn(FixtureDto.getCompanyDto());
        // When
        mvc.perform(
                get("/user/company")
                        .with(authentication(authentication))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("user/company"))
                .andExpect(model().attributeExists("companyData"));
        // Then
        then(userAccountService).should().getCompany("userCompany@gmail.com");
    }

    @DisplayName("[POST] 기업 정보 수정 요청")
    @Test
    void updateCompany() throws Exception {
        // Given
        given(userAccountService.getCompany("userCompany@gmail.com")).willReturn(FixtureDto.getCompanyDto());
        willDoNothing().given(companyService).updateCompany(any(CompanyDto.class));
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("userCompany", UserRole.COMPANY);
        // When
        mvc.perform(
                post("/user/company")
                        .with(authentication(authentication))
                        .with(csrf())
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user/company"))
                .andExpect(redirectedUrl("/user/company"));
        // Then
        then(userAccountService).should().getCompany("userCompany@gmail.com");
        then(companyService).should().updateCompany(any(CompanyDto.class));
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
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        // Then
        then(userAccountService).should().getAlarms(anyString());
    }

    @DisplayName("[GET] SSE 연결")
    @Test
    void subscribe() throws Exception {
        // Given
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("jujoo042386", UserRole.COMPANY);
        given(alarmService.connectAlarm(anyString())).willReturn(any(SseEmitter.class));
        // When
        mvc.perform(
                get("/user/alarm/subscribe")
                        .with(authentication(authentication))
        )
                .andExpect(status().isOk());
        // Then
        then(alarmService).should().connectAlarm(anyString());
    }
}
