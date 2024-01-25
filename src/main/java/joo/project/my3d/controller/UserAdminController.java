package joo.project.my3d.controller;

import joo.project.my3d.dto.CompanyDto;
import joo.project.my3d.dto.request.CompanyAdminRequest;
import joo.project.my3d.dto.request.UserAdminRequest;
import joo.project.my3d.dto.response.AlarmResponse;
import joo.project.my3d.dto.response.ApiResponse;
import joo.project.my3d.dto.response.CompanyAdminResponse;
import joo.project.my3d.dto.response.UserAdminResponse;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.service.AlarmServiceInterface;
import joo.project.my3d.service.CompanyServiceInterface;
import joo.project.my3d.service.UserAccountServiceInterface;
import joo.project.my3d.service.impl.AlarmService;
import joo.project.my3d.service.impl.CompanyService;
import joo.project.my3d.service.impl.UserAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAccountServiceInterface userAccountService;
    private final CompanyServiceInterface companyService;
    private final AlarmServiceInterface<SseEmitter> alarmService;

    /**
     * 사용자 정보 요청
     */
    @GetMapping("/account")
    public ResponseEntity<UserAdminResponse> userData(@AuthenticationPrincipal BoardPrincipal boardPrincipal) {

        return ResponseEntity.ok(
                UserAdminResponse.of(
                        boardPrincipal.nickname(),
                        boardPrincipal.phone(),
                        boardPrincipal.email(),
                        boardPrincipal.address()
                )
        );
    }

    /**
     * 사용자 정보 수정 요청
     */
    @PostMapping("/account")
    public ResponseEntity<ApiResponse> updateUserData(UserAdminRequest userAdminRequest) {
        userAccountService.updateUser(userAdminRequest.toDto());

        return ResponseEntity.ok(
                ApiResponse.of("You successfully updated user account")
        );
    }

    /**
     * 비밀번호 변경 페이지 요청(프론트엔드 작업시 불필요하면 삭제)
     */
    @GetMapping("/password")
    public ResponseEntity<Void> password() {

        return ResponseEntity.ok(null);
    }

    /**
     * 비밀번호 변경 요청
     */
    @PostMapping("/password")
    public ResponseEntity<ApiResponse> changePassword(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            UserAdminRequest userAdminRequest
    ) {
        userAccountService.changePassword(boardPrincipal.email(), userAdminRequest.password());

        return ResponseEntity.ok(
                ApiResponse.of("You successfully changed password")
        );
    }

    /**
     * 기업 정보 괸리 페이지 요청
     */
    @GetMapping("/company")
    public ResponseEntity<CompanyAdminResponse> company(@AuthenticationPrincipal BoardPrincipal boardPrincipal) {
        CompanyDto company = companyService.getCompany(boardPrincipal.email());

        return ResponseEntity.ok(
                CompanyAdminResponse.of(
                        company.companyName(),
                        company.homepage()
                )
        );
    }

    /**
     * 기업 정보 수정 요청
     */
    @PostMapping("/company")
    public ResponseEntity<ApiResponse> updateCompany(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            CompanyAdminRequest companyAdminRequest
    ) {
        CompanyDto company = companyService.getCompany(boardPrincipal.email());
        companyService.updateCompany(companyAdminRequest.toDto(company.id()));

        return ResponseEntity.ok(
                ApiResponse.of("You successfully updated company")
        );
    }

    /**
     * 현재 로그인한 사용자에게 온 알람 조회
     */
    @GetMapping("/alarm")
    public ResponseEntity<List<AlarmResponse>> getAlarms(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        return ResponseEntity.ok(
                    alarmService.getAlarms(boardPrincipal.email()).stream()
                            .map(AlarmResponse::from)
                            .collect(Collectors.toList())
        );
    }

    /**
     * SSE 연결 요청
     */
    @GetMapping("/alarm/subscribe")
    public ResponseEntity<SseEmitter> subscribe(@AuthenticationPrincipal BoardPrincipal boardPrincipal) {

        return ResponseEntity.ok(
                alarmService.connectAlarm(boardPrincipal.email())
        );
    }
}
