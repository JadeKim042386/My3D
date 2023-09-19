package joo.project.my3d.service;

import joo.project.my3d.repository.EmitterRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@DisplayName("비지니스 로직 - 알람")
@ExtendWith(MockitoExtension.class)
class AlarmServiceTest {
    @InjectMocks private AlarmService alarmService;
    @Mock private EmitterRepository emitterRepository;

    @DisplayName("알람 전송")
    @Test
    void sendAlarm() {
        // Given
        String email = "jujoo042386@gmail.com";
        Long alarmId = 1L;
        SseEmitter sseEmitter = new SseEmitter(60L * 60 * 1000);
        given(emitterRepository.get(email)).willReturn(Optional.of(sseEmitter));
        // When
        alarmService.send(email, alarmId);
        // Then
        then(emitterRepository).should().get(email);
    }

    @DisplayName("알람 전송 - 연결 실패")
    @Test
    void sendAlarmFailed() {
        // Given
        String email = "jujoo042386@gmail.com";
        Long alarmId = 1L;
        given(emitterRepository.get(email)).willReturn(Optional.empty());
        // When
        alarmService.send(email, alarmId);
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
