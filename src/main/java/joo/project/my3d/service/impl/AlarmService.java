package joo.project.my3d.service.impl;

import joo.project.my3d.domain.Alarm;
import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.AlarmType;
import joo.project.my3d.dto.AlarmDto;
import joo.project.my3d.dto.response.AlarmResponse;
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
    public List<AlarmDto> getAlarms(Long receiverId) {
        return alarmRepository.findAllByReceiverId(receiverId).stream()
                .map(AlarmDto::from)
                .sorted(Comparator.comparing(AlarmDto::createdAt).reversed())
                .toList();
    }

    /**
     * @param targetId 댓글 id
     * @throws AlarmException 알람 전송 실패 예외
     */
    @Transactional
    @Override
    public void send(Article article, Long targetId, UserAccount sender, UserAccount receiver) {
        Alarm alarm = saveAlarm(targetId, article, sender, receiver);
        emitterRepository
                .get(receiver.getEmail())
                .ifPresentOrElse(
                        sseEmitter -> {
                            try {
                                sseEmitter.send(SseEmitter.event()
                                        .id(String.valueOf(alarm.getId()))
                                        .name(ALARM_NAME)
                                        .data(AlarmResponse.fromEntity(alarm)));
                            } catch (IOException e) {
                                emitterRepository.delete(receiver.getEmail());
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
    public Alarm saveAlarm(Long targetId, Article article, UserAccount sender, UserAccount receiver) {
        return alarmRepository.save(Alarm.of(AlarmType.NEW_COMMENT, targetId, false, article, sender, receiver));
    }
}
