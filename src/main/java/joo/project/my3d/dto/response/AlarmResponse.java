package joo.project.my3d.dto.response;

import joo.project.my3d.domain.Alarm;
import joo.project.my3d.domain.constant.AlarmType;
import joo.project.my3d.dto.AlarmDto;
import joo.project.my3d.utils.LocalDateTimeUtils;

import java.time.LocalDateTime;

public record AlarmResponse(
        Long id,
        AlarmType alarmType,
        String fromUserNickname,
        Long articleId,
        boolean isChecked,
        String createdAt,
        String modifiedAt) {

    public static AlarmResponse of(
            Long id,
            AlarmType alarmType,
            String fromUserNickname,
            Long targetId,
            boolean isChecked,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt) {
        return new AlarmResponse(
                id,
                alarmType,
                fromUserNickname,
                targetId,
                isChecked,
                LocalDateTimeUtils.passedTime(createdAt),
                LocalDateTimeUtils.passedTime(modifiedAt));
    }

    public static AlarmResponse fromDto(AlarmDto dto) {
        return AlarmResponse.of(
                dto.id(),
                dto.alarmType(),
                dto.fromUserNickname(),
                dto.articleId(),
                dto.isChecked(),
                dto.createdAt(),
                dto.modifiedAt());
    }

    public static AlarmResponse fromEntity(Alarm alarm) {
        return AlarmResponse.of(
                alarm.getId(),
                alarm.getAlarmType(),
                alarm.getSender().getNickname(),
                alarm.getArticle().getId(),
                alarm.isChecked(),
                alarm.getCreatedAt(),
                alarm.getModifiedAt());
    }
}
