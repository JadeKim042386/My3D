package joo.project.my3d.dto;

import joo.project.my3d.domain.Alarm;
import joo.project.my3d.domain.constant.AlarmType;

import java.time.LocalDateTime;

public record AlarmDto(
        Long id,
        AlarmType alarmType,
        String fromUserNickname,
        Long articleId,
        boolean isChecked,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt) {

    public static AlarmDto of(
            Long id,
            AlarmType alarmType,
            String fromUserNickname,
            Long articleId,
            boolean isChecked,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt) {
        return new AlarmDto(id, alarmType, fromUserNickname, articleId, isChecked, createdAt, modifiedAt);
    }

    public static AlarmDto from(Alarm alarm, Long articleId) {
        return AlarmDto.of(
                alarm.getId(),
                alarm.getAlarmType(),
                alarm.getSender().getNickname(),
                articleId,
                alarm.isChecked(),
                alarm.getCreatedAt(),
                alarm.getModifiedAt());
    }
}
