package joo.project.my3d.dto;

import joo.project.my3d.domain.Alarm;
import joo.project.my3d.domain.constant.AlarmType;

import java.time.LocalDateTime;

public record AlarmDto(
        Long id,
        AlarmType alarmType,
        String fromUserNickname,
        Long targetId,
        boolean isChecked,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy

) {
    public static AlarmDto of(Long id, AlarmType alarmType, String fromUserNickname, Long targetId, boolean isChecked, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new AlarmDto(id, alarmType, fromUserNickname, targetId, isChecked, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static AlarmDto from(Alarm alarm) {
        return AlarmDto.of(
                alarm.getId(),
                alarm.getAlarmType(),
                alarm.getFromUserNickname(),
                alarm.getTargetId(),
                alarm.isChecked(),
                alarm.getCreatedAt(),
                alarm.getCreatedBy(),
                alarm.getModifiedAt(),
                alarm.getModifiedBy()
        );
    }
}
