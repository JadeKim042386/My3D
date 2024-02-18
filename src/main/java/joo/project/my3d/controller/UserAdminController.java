package joo.project.my3d.controller;

import joo.project.my3d.aop.TimeTrace;
import joo.project.my3d.dto.CompanyDto;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.dto.response.CompanyAdminResponse;
import joo.project.my3d.dto.response.UserAdminResponse;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.service.CompanyServiceInterface;
import joo.project.my3d.service.UserAccountServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class UserAdminController {

    private final UserAccountServiceInterface userAccountService;
    private final CompanyServiceInterface companyService;

    /**
     * 유저 정보 페이지
     *
     * {
     *     nickname: String,
     *     phone: String,
     *     email: String,
     *     detail: String,
     *     street: String,
     *     zipcode: String
     * }
     */
    @TimeTrace
    @GetMapping
    public String account(@AuthenticationPrincipal BoardPrincipal boardPrincipal, Model model) {
        UserAccountDto userAccountDto = userAccountService.searchUserDto(boardPrincipal.email());
        model.addAttribute(
                "userData",
                UserAdminResponse.of(
                        userAccountDto.nickname(),
                        userAccountDto.phone(),
                        userAccountDto.email(),
                        userAccountDto.addressDto()));
        return "admin/account";
    }

    /**
     * 기업 정보 페이지
     *
     * {
     *     companyName: String,
     *     homepage: String
     * }
     */
    @TimeTrace
    @GetMapping("/company")
    public String company(@AuthenticationPrincipal BoardPrincipal boardPrincipal, Model model) {
        CompanyDto company = companyService.getCompanyDto(boardPrincipal.id());
        model.addAttribute("companyData", CompanyAdminResponse.of(company.companyName(), company.homepage()));
        return "admin/company";
    }
    /**
     * 비밀번호 변경 페이지 요청(프론트엔드 작업시 불필요하면 삭제)
     */
    @GetMapping("/password")
    public String password() {
        return "admin/password";
    }
}
