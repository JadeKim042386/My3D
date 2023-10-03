package joo.project.my3d.service;

import joo.project.my3d.domain.Alarm;
import joo.project.my3d.exception.AlarmException;
import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.repository.AlarmRepository;
import joo.project.my3d.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlarmService {
    private final EmitterRepository emitterRepository;
    private final AlarmRepository alarmRepository;
    private static final Long DEFAULT_TIMEOUT = 60L * 60 * 1000; //1시간
    private static final String ALARM_NAME = "alarm";

    public void send(String email, Long alarmId) {
        emitterRepository.get(email).ifPresentOrElse(sseEmitter -> {
            try {
                sseEmitter.send(SseEmitter.event().id(alarmId.toString()).name(ALARM_NAME).data("new alarm"));
            } catch (IOException e) {
                emitterRepository.delete(email);
                throw new AlarmException(ErrorCode.ALARM_CONNECT_ERROR, e);
            }
        }, () -> log.info("Emitter를 찾을 수 없습니다."));
    }

    public SseEmitter connectAlarm(String email) {
        SseEmitter sseEmitter = new SseEmitter(DEFAULT_TIMEOUT);
        emitterRepository.save(email, sseEmitter);
        sseEmitter.onCompletion(() -> emitterRepository.delete(email));
        sseEmitter.onTimeout(() -> emitterRepository.delete(email));

        try {
            sseEmitter.send(SseEmitter.event().id("").name(ALARM_NAME).data("connect completed"));
        } catch (IOException e) {
            throw new AlarmException(ErrorCode.ALARM_CONNECT_ERROR, e);
        }

        return sseEmitter;
    }

    @Transactional
    public void checkAlarm(Long alarmId) {
        Alarm alarm = alarmRepository.getReferenceById(alarmId);
        alarm.setChecked(true);
    }
}
