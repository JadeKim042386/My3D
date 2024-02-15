package joo.project.my3d.service.impl;

import joo.project.my3d.domain.Alarm;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.AlarmType;
import joo.project.my3d.dto.AlarmDto;
import joo.project.my3d.exception.AlarmException;
import joo.project.my3d.exception.CommentException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.repository.AlarmRepository;
import joo.project.my3d.repository.ArticleCommentRepository;
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
    private final ArticleCommentRepository articleCommentRepository;

    @Override
    public List<AlarmDto> getAlarms(Long receiverId) {
        return alarmRepository.findAllByReceiverId(receiverId).stream()
                .map(alarm -> AlarmDto.from(alarm, getArticleId(alarm)))
                .sorted(Comparator.comparing(AlarmDto::createdAt).reversed())
                .toList();
    }

    /**
     * @param targetId 댓글 id
     * @throws AlarmException 알람 전송 실패 예외
     */
    @Transactional
    @Override
    public void send(Long targetId, UserAccount sender, UserAccount receiver) {
        Long alarmId = saveAlarm(targetId, sender, receiver).getId();
        emitterRepository
                .get(receiver.getEmail())
                .ifPresentOrElse(
                        sseEmitter -> {
                            try {
                                sseEmitter.send(SseEmitter.event()
                                        .id(String.valueOf(alarmId))
                                        .name(ALARM_NAME)
                                        .data("new alarm"));
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
    public Alarm saveAlarm(Long targetId, UserAccount sender, UserAccount receiver) {
        return alarmRepository.save(Alarm.of(AlarmType.NEW_COMMENT, targetId, false, sender, receiver));
    }

    /**
     * 알람이 발생한 게시글 id를 조회합니다. 알람을 통해 게시글로 이동하기 위해 필요합니다.
     */
    private Long getArticleId(Alarm alarm) {
        if (alarm.getAlarmType() == AlarmType.NEW_COMMENT) {
            return articleCommentRepository
                    .findArticleIdById(alarm.getTargetId())
                    .orElseThrow(() -> new CommentException(ErrorCode.COMMENT_NOT_FOUND));
        }
        throw new AlarmException(ErrorCode.INVALID_REQUEST);
    }
}
