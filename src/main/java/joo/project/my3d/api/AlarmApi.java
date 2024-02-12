package joo.project.my3d.api;

import joo.project.my3d.dto.response.AlarmResponse;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.service.AlarmServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/alarm")
@RequiredArgsConstructor
public class AlarmApi {
    private final AlarmServiceInterface<SseEmitter> alarmService;

    /**
     * 현재 로그인한 사용자에게 온 알람 조회
     */
    @GetMapping
    public ResponseEntity<List<AlarmResponse>> getAlarms(@AuthenticationPrincipal BoardPrincipal boardPrincipal) {
        return ResponseEntity.ok(alarmService.getAlarms(boardPrincipal.email()).stream()
                .map(AlarmResponse::from)
                .collect(Collectors.toList()));
    }

    /**
     * SSE 연결 요청
     */
    @GetMapping("/subscribe")
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal BoardPrincipal boardPrincipal) {

        return ResponseEntity.ok(alarmService.connectAlarm(boardPrincipal.email()));
    }
}
