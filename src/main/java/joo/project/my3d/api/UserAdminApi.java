package joo.project.my3d.api;

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
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class UserAdminApi {

    private final UserAccountServiceInterface userAccountService;
    private final CompanyServiceInterface companyService;

    /**
     * 사용자 정보 수정 요청
     */
    @PostMapping
    public ResponseEntity<ApiResponse> updateUserData(UserAdminRequest userAdminRequest) {
        userAccountService.updateUser(userAdminRequest.toDto());

        return ResponseEntity.ok(
                ApiResponse.of("You successfully updated user account")
        );
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
}
