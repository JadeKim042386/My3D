package joo.project.my3d.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AlarmType {
    NEW_COMMENT("새로운 댓글!");

    private final String alarmText;
}
