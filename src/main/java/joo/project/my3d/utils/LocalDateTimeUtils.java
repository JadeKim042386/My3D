package joo.project.my3d.utils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class LocalDateTimeUtils {

    /**
     * 게시글 작성한 시간으로부터 얼만큼의 시간이 지났는지를 계산하여 다음과 같이 반환
     * <br><br>
     * - 방금 전<br>
     * - ~분 전<br>
     * - ~시간 전<br>
     * - ~일 전<br>
     * - ~개월 전<br>
     * - ~년 전<br>
     */
    public static String passedTime(LocalDateTime createdAt){
        LocalDateTime now = LocalDateTime.now();
        //방금 전
        long seconds = ChronoUnit.SECONDS.between(createdAt, now);
        if (seconds < 60) {
            return "방금 전";
        }
        //~분 전
        long minutes = ChronoUnit.MINUTES.between(createdAt, now);
        if (minutes < 60) {
            return minutes + "분 전";
        }
        //~시간 전
        long hours = ChronoUnit.HOURS.between(createdAt, now);
        if (hours < 12) {
            return hours + "시간 전";
        }
        //~일 전
        long days = ChronoUnit.DAYS.between(createdAt, now);
        long months = ChronoUnit.MONTHS.between(createdAt, now);
        if (months == 0) {
            return days + "일 전";
        }
        //~개월 전
        if (months < 12) {
            return months + "개월 전";
        }

        //~년 전
        long years = ChronoUnit.YEARS.between(createdAt, now);
        return years + "년 전";
    }
}
