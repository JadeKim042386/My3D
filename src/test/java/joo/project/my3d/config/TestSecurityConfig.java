package joo.project.my3d.config;

import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.service.UserAccountService;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.event.annotation.BeforeTestMethod;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;

@Import(SecurityConfig.class)
public class TestSecurityConfig {
    @MockBean private UserAccountService userAccountService;

    @BeforeTestMethod
    public void securitySetUp() {
        given(userAccountService.searchUser(anyString()))
                .willReturn(Optional.of(createUserAccountDto()));
        given(userAccountService.saveUser(anyString(), anyString(), anyString(), anyString(), any(UserRole.class)))
                .willReturn(createUserAccountDto());
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                "jooTest",
                "pw",
                "joo-test@gmail.com",
                "joo-test",
                UserRole.USER
        );
    }
}
