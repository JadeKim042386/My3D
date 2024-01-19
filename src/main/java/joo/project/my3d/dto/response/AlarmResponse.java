package joo.project.my3d.dto.response;

import joo.project.my3d.domain.constant.AlarmType;
import joo.project.my3d.dto.AlarmDto;
import joo.project.my3d.utils.LocalDateTimeUtils;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public record AlarmResponse(
        Long id,
        AlarmType alarmType,
        String fromUserNickname,
        Long targetId,
        String text,
        boolean isChecked,
        String createdAt,
        String createdBy,
        String modifiedAt,
        String modifiedBy

) {
    public static AlarmResponse of(Long id, AlarmType alarmType, String fromUserNickname, Long targetId, String text, boolean isChecked, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new AlarmResponse(id, alarmType, fromUserNickname, targetId, text, isChecked, LocalDateTimeUtils.passedTime(createdAt), createdBy, LocalDateTimeUtils.passedTime(modifiedAt), modifiedBy);
    }

    public static AlarmResponse from(AlarmDto dto) {
        return AlarmResponse.of(
                dto.id(),
                dto.alarmType(),
                dto.fromUserNickname(),
                dto.targetId(),
                dto.alarmType().getAlarmText(),
                dto.isChecked(),
                dto.createdAt(),
                dto.createdBy(),
                dto.modifiedAt(),
                dto.modifiedBy()
        );
    }
}
