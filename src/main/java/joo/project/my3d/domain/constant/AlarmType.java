package joo.project.my3d.domain.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AlarmType {
    NEW_COMMENT_ON_POST("새로운 댓글!"),
    NEW_LIKE_ON_POST("좋아요!"),
    NEW_ORDER("새로운 주문!")
    ;

    private final String alarmText;
}
