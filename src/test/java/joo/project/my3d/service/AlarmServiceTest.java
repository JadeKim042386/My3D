package joo.project.my3d.service;

import joo.project.my3d.domain.Alarm;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.repository.AlarmRepository;
import joo.project.my3d.repository.EmitterRepository;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@DisplayName("비지니스 로직 - 알람")
@ExtendWith(MockitoExtension.class)
class AlarmServiceTest {
    @InjectMocks private AlarmService alarmService;
    @Mock private AlarmRepository alarmRepository;
    @Mock private EmitterRepository emitterRepository;

    @DisplayName("알람 조회")
    @Test
    void getAlarms() {
        // Given
        UserAccount userAccount = Fixture.getUserAccount();
        given(alarmRepository.findAllByUserAccount_Email(userAccount.getEmail())).willReturn(List.of());
        // When
        alarmService.getAlarms(userAccount.getEmail());
        // Then
        then(alarmRepository).should().findAllByUserAccount_Email(userAccount.getEmail());
    }

    @DisplayName("알람 전송")
    @Test
    void sendAlarm() throws IllegalAccessException {
        // Given
        String email = "jujoo042386@gmail.com";
        String nickname = "nickname";
        UserAccount userAccount = Fixture.getUserAccount();
        Long articleId = 1L;
        SseEmitter sseEmitter = new SseEmitter(60L * 60 * 1000);
        Alarm alarm = Fixture.getAlarm(userAccount);
        FieldUtils.writeField(alarm, "id", 1L, true);
        given(alarmRepository.save(any())).willReturn(alarm);
        given(emitterRepository.get(email)).willReturn(Optional.of(sseEmitter));
        // When
        alarmService.send(email, nickname, articleId, userAccount);
        // Then
        then(emitterRepository).should().get(email);
    }

    @DisplayName("알람 전송 - 연결 실패")
    @Test
    void sendAlarmFailed() {
        // Given
        String email = "jujoo042386@gmail.com";
        String nickname = "nickname";
        UserAccount userAccount = Fixture.getUserAccount();
        Long articleId = 1L;
        given(alarmRepository.save(any())).willReturn(Fixture.getAlarm(userAccount));
        given(emitterRepository.get(email)).willReturn(Optional.empty());
        // When
        alarmService.send(email, nickname, articleId, userAccount);
        // Then
        then(emitterRepository).should().get(email);
    }

    @DisplayName("SSE 연결")
    @Test
    void connectAlarm() {
        // Given
        String email = "jujoo042386@gmail.com";
        SseEmitter sseEmitter = new SseEmitter(10L * 1000);
        given(emitterRepository.save(eq(email), any(SseEmitter.class))).willReturn(sseEmitter);
        // When
        alarmService.connectAlarm(email);
        // Then
        then(emitterRepository).should().save(eq(email), any(SseEmitter.class));
    }
}
