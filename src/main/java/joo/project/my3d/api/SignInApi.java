package joo.project.my3d.api;

import joo.project.my3d.dto.request.UserLoginRequest;
import joo.project.my3d.dto.response.ApiResponse;
import joo.project.my3d.dto.response.LoginResponse;
import joo.project.my3d.exception.AuthException;
import joo.project.my3d.service.UserAccountServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/api/v1/signin")
@RequiredArgsConstructor
public class SignInApi {

    private final UserAccountServiceInterface userAccountService;
    private final SecurityContextLogoutHandler logoutHandler;

    /**
     * 로그인 요청
     * @param loginRequest 로그인 폼에 입력된 이메일, 비밀번호를 담은 DTO
     * @throws AuthException 로그인에 실패할 경우 발생하는 예외
     */
    @PostMapping
    public ResponseEntity<LoginResponse> requestLogin(UserLoginRequest loginRequest) {

        return ResponseEntity.ok(
                userAccountService.login(loginRequest.email(), loginRequest.password())
        );
    }

    /**
     * oauth 로그인 후 로그인 처리 또는 oauth 정보 반환
     * @param signup 회원가입 여부
     */
    @GetMapping("/oauth")
    public ResponseEntity<LoginResponse> oauthResponse(
            @RequestParam String email,
            @RequestParam String nickname,
            @RequestParam boolean signup
    ) {
        //회원가입이 되어있다면 로그인처리
        if (signup) {
            return ResponseEntity.ok(userAccountService.oauthLogin(email, nickname));
        }
        //회원가입이 안되어있다면 email과 nickmame 전달하고 프론트엔드에서 처리하도록 적용
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(LoginResponse.of(email, nickname));
    }

    /**
     * 로그아웃 요청
     */
    @GetMapping("/logout")
    public ResponseEntity<ApiResponse> requestLogout(Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        logoutHandler.logout(request, response, authentication);

        return ResponseEntity.ok(ApiResponse.of("You successfully logout"));
    }


}
