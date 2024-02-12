package joo.project.my3d.api;

import joo.project.my3d.dto.request.UserLoginRequest;
import joo.project.my3d.dto.response.LoginResponse;
import joo.project.my3d.dto.response.UserInfo;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.exception.AuthException;
import joo.project.my3d.service.UserAccountServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@RestController
@RequestMapping("/api/v1/signin")
@RequiredArgsConstructor
public class SignInApi {

    private final UserAccountServiceInterface userAccountService;

    /**
     * 로그인 요청
     * @param loginRequest 로그인 폼에 입력된 이메일, 비밀번호를 담은 DTO
     * @throws AuthException 로그인에 실패할 경우 발생하는 예외
     */
    @PostMapping
    public ResponseEntity<LoginResponse> requestLogin(UserLoginRequest loginRequest) {

        return ResponseEntity.ok(userAccountService.login(loginRequest.email(), loginRequest.password()));
    }

    /**
     * oauth 로그인 후 로그인 처리 또는 oauth 정보 반환
     * @param signup 회원가입 여부
     */
    @GetMapping("/oauth")
    public ResponseEntity<LoginResponse> oauthResponse(
            @RequestParam String email,
            @RequestParam String nickname,
            @RequestParam boolean signup,
            RedirectAttributes redirectAttributes) {
        // 회원가입이 안되어있다면 회원가입 페이지로 email과 nickmame와 같이 redirect
        if (!signup) {
            redirectAttributes.addFlashAttribute("email", email);
            redirectAttributes.addFlashAttribute("nickname", nickname);
        }

        // 회원가입이 되어있다면 로그인처리
        return ResponseEntity.ok(userAccountService.oauthLogin(email, nickname));
    }

    /**
     * 토큰을 통해 유저의 Role을 반환함과 동시에 validation도 확인
     */
    @GetMapping("/info")
    public ResponseEntity<UserInfo> parseSpecificationFromToken(
            @AuthenticationPrincipal BoardPrincipal boardPrincipal) {

        return ResponseEntity.ok(
                UserInfo.of(boardPrincipal.email(), boardPrincipal.nickname(), boardPrincipal.getUserRole()));
    }
}
