package joo.project.my3d.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * OAuth2 로그인 테스트를 위해 추가했으며 추후 Deprecate 예정
 */
@Deprecated
@Controller
@RequiredArgsConstructor
public class OAuth2LoginController {

    @GetMapping("/oauth")
    @PreAuthorize("hasRole('ANONYMOUS')")
    public String index() {
        return "/account/login";
    }
}
