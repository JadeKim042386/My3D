package joo.project.my3d.api;

import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.fixture.FixtureCookie;
import joo.project.my3d.service.AlarmServiceInterface;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@DisplayName("View 컨트롤러 - 알람")
@Import(TestSecurityConfig.class)
@WebMvcTest(AlarmApi.class)
class AlarmApiTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private AlarmServiceInterface alarmService;

    private static Cookie userCookie = FixtureCookie.createUserCookie();

    @DisplayName("[GET] 알람 리스트 조회")
    @Test
    void getAlarms() throws Exception {
        // Given
        given(alarmService.getAlarms(anyString())).willReturn(List.of());
        // When
        mvc.perform(get("/api/v1/alarm").cookie(userCookie))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(0));
        // Then
    }

    @DisplayName("[GET] SSE 연결")
    @Test
    void subscribe() throws Exception {
        // Given
        given(alarmService.connectAlarm(anyString())).willReturn(null);
        // When
        mvc.perform(get("/api/v1/alarm/subscribe").cookie(userCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").doesNotExist());
        // Then
    }
}