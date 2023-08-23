package joo.project.my3d.service;

import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.repository.UserAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ActiveProfiles("test")
@DisplayName("비지니스 로직 - 회원 계정")
@ExtendWith(MockitoExtension.class)
class UserAccountServiceTest {
    @InjectMocks private UserAccountService userAccountService;
    @Mock private UserAccountRepository userAccountRepository;

    @DisplayName("회원 목록 조회")
    @Test
    void getUserAccounts() {
        // Given
        given(userAccountRepository.findAll()).willReturn(List.of());
        // When
        userAccountService.findAllUser();
        // Then
        then(userAccountRepository).should().findAll();
    }

    @DisplayName("회원 조회")
    @Test
    void getUserAccount() {
        // Given
        String email = "a@gmail.com";
        given(userAccountRepository.findById(anyString())).willReturn(Optional.empty());
        // When
        userAccountService.searchUser(email);
        // Then
        then(userAccountRepository).should().findById(anyString());
    }

    @DisplayName("회원 추가 - 필드 주입")
    @Test
    void saveUserAccountByFields() {
        // Given
        UserAccount userAccount = Fixture.getUserAccount("a2@gmail.com", "pw", "a2", true, UserRole.USER);
        given(userAccountRepository.save(any(UserAccount.class))).willReturn(userAccount);
        // When
        userAccountService.saveUser("a2@gmail.com", "pw", "a2", UserRole.USER, true);
        // Then
        then(userAccountRepository).should().save(any(UserAccount.class));
    }

    @DisplayName("회원 추가 - 엔티티 주입")
    @Test
    void saveUserAccountByEntity() {
        // Given
        UserAccount userAccount = Fixture.getUserAccount("a2@gmail.com", "pw", "a2", true, UserRole.USER);
        given(userAccountRepository.save(any(UserAccount.class))).willReturn(any(UserAccount.class));
        // When
        userAccountService.saveUser(userAccount);
        // Then
        then(userAccountRepository).should().save(any(UserAccount.class));
    }
}