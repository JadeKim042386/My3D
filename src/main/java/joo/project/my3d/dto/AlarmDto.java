package joo.project.my3d.dto;

import joo.project.my3d.domain.Alarm;
import joo.project.my3d.domain.constant.AlarmType;

import java.time.LocalDateTime;

public record AlarmDto(
        Long id,
        AlarmType alarmType,
        String fromUserEmail,
        Long targetId,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy

) {
    public static AlarmDto of(Long id, AlarmType alarmType, String fromUserEmail, Long targetId, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new AlarmDto(id, alarmType, fromUserEmail, targetId, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static AlarmDto from(Alarm alarm) {
        return AlarmDto.of(
                alarm.getId(),
                alarm.getAlarmType(),
                alarm.getFromUserEmail(),
                alarm.getTargetId(),
                alarm.getCreatedAt(),
                alarm.getCreatedBy(),
                alarm.getModifiedAt(),
                alarm.getModifiedBy()
        );
    }
}
