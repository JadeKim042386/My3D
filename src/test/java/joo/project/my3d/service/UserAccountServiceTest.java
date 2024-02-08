package joo.project.my3d.service;

import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.dto.response.LoginResponse;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.repository.UserAccountRepository;
import joo.project.my3d.repository.UserRefreshTokenRepository;
import joo.project.my3d.security.TokenProvider;
import joo.project.my3d.service.impl.UserAccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ActiveProfiles("test")
@DisplayName("비지니스 로직 - 회원 계정")
@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {
    @InjectMocks private UserAccountService userAccountService;
    @Mock private UserAccountRepository userAccountRepository;
    @Mock private UserRefreshTokenRepository userRefreshTokenRepository;
    @Mock private TokenProvider tokenProvider;
    @Mock private BCryptPasswordEncoder encoder;

    @DisplayName("회원 조회")
    @Test
    void getUserAccount() {
        // Given
        String email = "jk042386@gmail.com";
        given(userAccountRepository.findByEmail(anyString())).willReturn(Optional.of(Fixture.getUserAccount()));
        // When
        UserAccountDto userAccountDto = userAccountService.searchUser(email);
        // Then
        assertThat(userAccountDto.email()).isEqualTo(email);
    }

    @DisplayName("회원 추가 - 엔티티 주입")
    @Test
    void saveUserAccountByEntity() {
        // Given
        UserAccount userAccount = Fixture.getUserAccount("a2@gmail.com", "pw", "a2", UserRole.USER);
        given(userAccountRepository.save(any(UserAccount.class))).willReturn(any(UserAccount.class));
        // When
        userAccountService.saveUser(userAccount);
        // Then
        then(userAccountRepository).should().save(any(UserAccount.class));
    }

    @DisplayName("회원 비밀번호 변경")
    @Test
    @WithAnonymousUser
    void changePassword() {
        // Given
        String email = "jk042386@gmail.com";
        String changedPassword = "changedPassword";
        given(userAccountRepository.getReferenceByEmail(email)).willReturn(Fixture.getUserAccount());
        // When
        userAccountService.changePassword(email, changedPassword);
        // Then
        then(userAccountRepository).should().getReferenceByEmail(email);
    }

    @DisplayName("회원 정보 수정")
    @Test
    void updateUser() {
        // Given
        String email = "jk042386@gmail.com";
        UserAccountDto userAccountDto = FixtureDto.getUserAccountDto();
        given(userAccountRepository.getReferenceByEmail(email)).willReturn(userAccountDto.toEntity("refreshToken"));
        // When
        userAccountService.updateUser(userAccountDto);
        // Then
        then(userAccountRepository).should().getReferenceByEmail(email);
    }

    @DisplayName("로그인")
    @Test
    void login() {
        //given
        String email = "abc@gmail.com";
        String password = "pw";
        given(userAccountRepository.findByEmail(anyString())).willReturn(Optional.of(Fixture.getUserAccount()));
        given(encoder.matches(anyString(), anyString())).willReturn(true);
        given(tokenProvider.generateAccessToken(anyString(), anyString(), anyString())).willReturn("accessToken");
        given(tokenProvider.generateRefreshToken()).willReturn("refreshToken");
        given(userRefreshTokenRepository.findById(anyLong())).willReturn(Optional.of(Fixture.getUserRefreshToken()));
        //when
        LoginResponse loginResponse = userAccountService.login(email, password);
        //then
        assertThat(loginResponse.accessToken()).isEqualTo("accessToken");
    }

    @DisplayName("로그인 - 주어진 이메일에 해당하는 유저가 존재하지 않을 경우")
    @Test
    void login_noFoundUser() {
        // Given
        String email = "abc@gmail.com";
        String password = "XX";
        given(userAccountRepository.findByEmail(anyString()))
                .willThrow(new UsernameNotFoundException("존재하지 않는 유저입니다."));
        // When
        assertThatThrownBy(() -> userAccountService.login(email, password))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasFieldOrPropertyWithValue("message", "존재하지 않는 유저입니다.");
        // Then
    }
}
