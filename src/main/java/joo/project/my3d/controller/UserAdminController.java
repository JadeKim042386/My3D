package joo.project.my3d.controller;

import joo.project.my3d.dto.CompanyDto;
import joo.project.my3d.dto.request.CompanyAdminRequest;
import joo.project.my3d.dto.request.UserAdminRequest;
import joo.project.my3d.dto.response.AlarmResponse;
import joo.project.my3d.dto.response.ApiResponse;
import joo.project.my3d.dto.response.CompanyAdminResponse;
import joo.project.my3d.dto.response.UserAdminResponse;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.service.AlarmService;
import joo.project.my3d.service.CompanyService;
import joo.project.my3d.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAccountService userAccountService;
    private final CompanyService companyService;
    private final AlarmService alarmService;

    /**
     * 사용자 정보 요청
     */
    @GetMapping("/account")
    public ApiResponse<UserAdminResponse> userData(@AuthenticationPrincipal BoardPrincipal boardPrincipal) {

        return ApiResponse.success(UserAdminResponse.of(
                boardPrincipal.nickname(),
                boardPrincipal.phone(),
                boardPrincipal.email(),
                boardPrincipal.address()
        ));
    }

    /**
     * 사용자 정보 수정 요청
     */
    @PostMapping("/account")
    public ApiResponse<Void> updateUserData(UserAdminRequest userAdminRequest) {
        userAccountService.updateUser(userAdminRequest.toDto());

        return ApiResponse.success();
    }

    /**
     * 비밀번호 변경 페이지 요청(프론트엔드 작업시 불필요하면 삭제)
     */
    @GetMapping("/password")
    public ApiResponse<Void> password() {

        return ApiResponse.success();
    }

    /**
     * 비밀번호 변경 요청
     */
    @PostMapping("/password")
    public ApiResponse<Void> changePassword(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            UserAdminRequest userAdminRequest
    ) {
        userAccountService.changePassword(boardPrincipal.email(), userAdminRequest.password());

        return ApiResponse.success();
    }

    /**
     * 기업 정보 괸리 페이지 요청
     */
    @GetMapping("/company")
    public ApiResponse<CompanyAdminResponse> company(@AuthenticationPrincipal BoardPrincipal boardPrincipal) {
        CompanyDto company = userAccountService.getCompany(boardPrincipal.email());

        return ApiResponse.success(CompanyAdminResponse.from(company));
    }

    /**
     * 기업 정보 수정 요청
     */
    @PostMapping("/company")
    public ApiResponse<Void> updateCompany(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            CompanyAdminRequest companyAdminRequest
    ) {
        CompanyDto company = userAccountService.getCompany(boardPrincipal.email());
        companyService.updateCompany(companyAdminRequest.toDto(company.id()));

        return ApiResponse.success();
    }

    /**
     * 현재 로그인한 사용자에게 온 알람 조회
     */
    @GetMapping("/alarm")
    public ApiResponse<List<AlarmResponse>> getAlarms(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        return ApiResponse.success(
                userAccountService.getAlarms(boardPrincipal.email()).stream()
                .map(AlarmResponse::from)
                .toList()
        );
    }

    /**
     * SSE 연결 요청
     */
    @GetMapping("/alarm/subscribe")
    public ApiResponse<SseEmitter> subscribe(@AuthenticationPrincipal BoardPrincipal boardPrincipal) {

        return ApiResponse.success(
                alarmService.connectAlarm(boardPrincipal.email())
        );
    }
}
