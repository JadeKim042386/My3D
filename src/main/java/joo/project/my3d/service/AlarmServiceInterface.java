package joo.project.my3d.service;

import joo.project.my3d.domain.Alarm;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.dto.AlarmDto;

import java.util.List;

public interface AlarmServiceInterface<T> {
    /**
     * 특정 유저의 모든 알람 조회
     */
    List<AlarmDto> getAlarms(Long receiverId);

    /**
     * 알람 전송
     */
    void send(Long targetId, UserAccount sender, UserAccount receiver);

    /**
     * 특정 유저와의 연결 시도
     */
    T connectAlarm(String email);

    /**
     * 알람 확인
     */
    void checkAlarm(Long alarmId);

    /**
     * 알람 저장
     */
    Alarm saveAlarm(Long targetId, UserAccount sender, UserAccount receiver);
}
