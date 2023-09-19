package joo.project.my3d.controller;

import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.CompanyDto;
import joo.project.my3d.dto.request.CompanyAdminRequest;
import joo.project.my3d.dto.request.OrdersRequest;
import joo.project.my3d.dto.request.UserAdminRequest;
import joo.project.my3d.dto.response.*;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.service.AlarmService;
import joo.project.my3d.service.CompanyService;
import joo.project.my3d.service.OrdersService;
import joo.project.my3d.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAccountService userAccountService;
    private final OrdersService ordersService;
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

    @GetMapping("/orders")
    public String orders(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            Model model
    ) {
        List<OrdersResponse> ordersResponses;
        if (boardPrincipal.getUserRole() == UserRole.USER) {
            ordersResponses = ordersService.getOrdersByEmail(boardPrincipal.email()).stream()
                    .map(OrdersResponse::from).toList();
        } else {
            CompanyDto company = userAccountService.getCompany(boardPrincipal.email());
            ordersResponses = ordersService.getOrdersByCompanyId(company.id()).stream()
                    .map(OrdersResponse::from).toList();
        }

        model.addAttribute("orders", ordersResponses);
        return "user/orders";
    }

    @PostMapping("/orders")
    public String updateOrdersStatus(
            @ModelAttribute("orders") OrdersRequest ordersRequest
    ) {
        ordersService.updateOrders(ordersRequest.toDto());

        return "redirect:/user/orders";
    }

    @GetMapping("/company")
    public String company(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            Model model
    ) {
        //기업 정보 전달
        CompanyDto company = userAccountService.getCompany(boardPrincipal.email());
        model.addAttribute("companyData", CompanyAdminResponse.from(company));

        return "user/company";
    }

    @PostMapping("/company")
    public String updateCompany(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            @ModelAttribute("companyData") CompanyAdminRequest companyAdminRequest
    ) {
        CompanyDto company = userAccountService.getCompany(boardPrincipal.email());
        companyService.updateCompany(companyAdminRequest.toDto(company.id()));

        return "redirect:/user/company";
    }

    @ResponseBody
    @GetMapping("/alarm")
    public Response<List<AlarmResponse>> getAlarms(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        List<AlarmResponse> alarmResponses = userAccountService.getAlarms(boardPrincipal.email()).stream()
                .map(AlarmResponse::from).toList();

        return Response.success(alarmResponses);
    }

    @ResponseBody
    @GetMapping("/alarm/subscribe")
    public SseEmitter subscribe(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        return alarmService.connectAlarm(boardPrincipal.email());
    }
}
