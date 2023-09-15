package joo.project.my3d.controller;

import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.dto.request.UserAdminRequest;
import joo.project.my3d.dto.response.UserAdminResponse;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.service.UserAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAccountService userAccountService;

    @GetMapping("/account")
    public String UserData(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            Model model
    ) {
        UserAdminResponse userAdminResponse = UserAdminResponse.of(
                boardPrincipal.getUserRole(),
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
        if (userAdminRequest.password() != null) {
            userAccountService.changePassword(userAdminRequest.email(), userAdminRequest.password());
        }
        return "redirect:/user/account";
    }
}
