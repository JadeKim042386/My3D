package joo.project.my3d.dto.response;

import joo.project.my3d.domain.constant.AlarmType;
import joo.project.my3d.dto.AlarmDto;

import java.time.LocalDateTime;

public record AlarmResponse(
        Long id,
        AlarmType alarmType,
        String fromUserEmail,
        Long targetId,
        String text,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy

) {
    public static AlarmResponse of(Long id, AlarmType alarmType, String fromUserEmail, Long targetId, String text, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new AlarmResponse(id, alarmType, fromUserEmail, targetId, text, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static AlarmResponse from(AlarmDto dto) {
        return AlarmResponse.of(
                dto.id(),
                dto.alarmType(),
                dto.fromUserEmail(),
                dto.targetId(),
                dto.alarmType().getAlarmText(),
                dto.createdAt(),
                dto.createdBy(),
                dto.modifiedAt(),
                dto.modifiedBy()
        );
    }
}
