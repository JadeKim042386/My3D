package joo.project.my3d.service.impl;

import joo.project.my3d.domain.Alarm;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.AlarmType;
import joo.project.my3d.dto.AlarmDto;
import joo.project.my3d.exception.AlarmException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.repository.AlarmRepository;
import joo.project.my3d.repository.EmitterRepository;
import joo.project.my3d.service.AlarmServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AlarmService implements AlarmServiceInterface<SseEmitter> {
    private static final Long SSE_TIMEOUT = 60L * 60 * 1000; // 1시간
    private static final String ALARM_NAME = "alarm";
    private final EmitterRepository emitterRepository;
    private final AlarmRepository alarmRepository;

    @Override
    public List<AlarmDto> getAlarms(String email) {
        return alarmRepository.findAllByUserAccount_Email(email).stream()
                .map(AlarmDto::from)
                .sorted(Comparator.comparing(AlarmDto::createdAt).reversed())
                .toList();
    }

    /**
     * @throws AlarmException 알람 전송 실패 예외
     */
    @Transactional
    @Override
    public void send(String email, String nickname, Long articleId, UserAccount userAccount) {
        Long alarmId = saveAlarm(articleId, nickname, userAccount).getId();
        emitterRepository
                .get(email)
                .ifPresentOrElse(
                        sseEmitter -> {
                            try {
                                sseEmitter.send(SseEmitter.event()
                                        .id(alarmId.toString())
                                        .name(ALARM_NAME)
                                        .data("new alarm"));
                            } catch (IOException e) {
                                emitterRepository.delete(email);
                                throw new AlarmException(ErrorCode.ALARM_CONNECT_ERROR, e);
                            }
                        },
                        () -> log.info("Emitter를 찾을 수 없습니다."));
    }

    /**
     * @throws AlarmException 알람 접속 실패 예외
     */
    @Override
    public SseEmitter connectAlarm(String email) {
        SseEmitter sseEmitter = new SseEmitter(SSE_TIMEOUT);
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
    @Override
    public void checkAlarm(Long alarmId) {
        Alarm alarm = alarmRepository.getReferenceById(alarmId);
        alarm.setChecked(true);
    }

    @Transactional
    @Override
    public Alarm saveAlarm(Long articleId, String nickname, UserAccount userAccount) {
        return alarmRepository.save(Alarm.of(AlarmType.NEW_COMMENT_ON_POST, nickname, articleId, false, userAccount));
    }
}
