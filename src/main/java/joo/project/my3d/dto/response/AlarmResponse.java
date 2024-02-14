package joo.project.my3d.dto.response;

import joo.project.my3d.domain.constant.AlarmType;
import joo.project.my3d.dto.AlarmDto;
import joo.project.my3d.utils.LocalDateTimeUtils;

import java.time.LocalDateTime;

public record AlarmResponse(
        Long id,
        AlarmType alarmType,
        String fromUserNickname,
        Long targetId,
        String text,
        boolean isChecked,
        String createdAt,
        String modifiedAt) {

    public static AlarmResponse of(
            Long id,
            AlarmType alarmType,
            String fromUserNickname,
            Long targetId,
            String text,
            boolean isChecked,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt) {
        return new AlarmResponse(
                id,
                alarmType,
                fromUserNickname,
                targetId,
                text,
                isChecked,
                LocalDateTimeUtils.passedTime(createdAt),
                LocalDateTimeUtils.passedTime(modifiedAt));
    }

    public static AlarmResponse from(AlarmDto dto) {
        return AlarmResponse.of(
                dto.id(),
                dto.alarmType(),
                dto.fromUserNickname(),
                dto.articleId(),
                dto.alarmType().getAlarmText(),
                dto.isChecked(),
                dto.createdAt(),
                dto.modifiedAt());
    }
}
