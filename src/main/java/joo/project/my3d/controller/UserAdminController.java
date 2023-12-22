package joo.project.my3d.controller;

import io.swagger.v3.oas.annotations.Operation;
import joo.project.my3d.dto.CompanyDto;
import joo.project.my3d.dto.request.CompanyAdminRequest;
import joo.project.my3d.dto.request.UserAdminRequest;
import joo.project.my3d.dto.response.AlarmResponse;
import joo.project.my3d.dto.response.CompanyAdminResponse;
import joo.project.my3d.dto.response.Response;
import joo.project.my3d.dto.response.UserAdminResponse;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.exception.AlarmException;
import joo.project.my3d.exception.CompanyException;
import joo.project.my3d.service.AlarmService;
import joo.project.my3d.service.CompanyService;
import joo.project.my3d.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAccountService userAccountService;
    private final CompanyService companyService;
    private final AlarmService alarmService;

    @GetMapping("/account")
    public String userData(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            Model model
    ) {
        UserAdminResponse userAdminResponse = UserAdminResponse.of(
                boardPrincipal.nickname(),
                boardPrincipal.phone(),
                boardPrincipal.email(),
                boardPrincipal.address()
        );
        model.addAttribute("userData", userAdminResponse);

        return "user/account";
    }

    @PostMapping("/account")
    public String updateUserData(
            @ModelAttribute("userData") UserAdminRequest userAdminRequest
    ) {
        userAccountService.updateUser(userAdminRequest.toDto());

        return "redirect:/user/account";
    }

    @GetMapping("/password")
    public String password() {

        return "user/password";
    }

    @PostMapping("/password")
    public String changePassword(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            @ModelAttribute("userData") UserAdminRequest userAdminRequest
    ) {
        userAccountService.changePassword(boardPrincipal.email(), userAdminRequest.password());

        return "redirect:/user/password";
    }

    /**
     * 기업 정보 괸리 페이지 요청
     */
    @GetMapping("/company")
    public String company(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            Model model
    ) {
        try {
            //기업 정보 전달
            CompanyDto company = userAccountService.getCompany(boardPrincipal.email());
            model.addAttribute("companyData", CompanyAdminResponse.from(company));
        } catch (UsernameNotFoundException e) {
            log.error("기업 정보 조회 실패 - {}", e.getMessage());
            return "user/account";
        }

        return "user/company";
    }

    /**
     * 기업 정보 수정 요청
     */
    @PostMapping("/company")
    public String updateCompany(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            @ModelAttribute("companyData") CompanyAdminRequest companyAdminRequest
    ) {
        try {
            CompanyDto company = userAccountService.getCompany(boardPrincipal.email());
            companyService.updateCompany(companyAdminRequest.toDto(company.id()));
        } catch (UsernameNotFoundException e) {
            log.error("기업 정보 조회 실패 - {}", e.getMessage());
        } catch (CompanyException e) {
            log.error("기업 정보 수정 실패 - {}", e.getMessage());
        }

        return "redirect:/user/company";
    }

    @Operation(summary = "현재 로그인한 사용자에게 온 알람 조회")
    @ResponseBody
    @GetMapping("/alarm")
    public Response<List<AlarmResponse>> getAlarms(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        List<AlarmResponse> alarmResponses = userAccountService.getAlarms(boardPrincipal.email()).stream()
                .map(AlarmResponse::from).toList();

        return Response.success(alarmResponses);
    }

    @Operation(summary = "SSE 연결 요청")
    @ResponseBody
    @GetMapping("/alarm/subscribe")
    public SseEmitter subscribe(
            HttpServletResponse response,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");

        try {
            return alarmService.connectAlarm(boardPrincipal.email());
        } catch (AlarmException e) {
            log.error("알람 연결 실패 - {}", e.getMessage());
            throw e;
        }
    }
}
