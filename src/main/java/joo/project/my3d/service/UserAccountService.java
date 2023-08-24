package joo.project.my3d.service;

import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;

    public List<UserAccountDto> findAllUser() {
        return userAccountRepository.findAll().stream()
                .map(UserAccountDto::from).toList();
    }

    public Optional<UserAccountDto> searchUser(String email) {
        return userAccountRepository.findById(email).map(UserAccountDto::from);
    }

    @Transactional
    public UserAccountDto saveUser(String email, String userPassword, String nickname, UserRole userRole, boolean signUp) {
        return UserAccountDto.from(
                userAccountRepository.save(
                    UserAccount.of(
                            email,
                            userPassword,
                            nickname,
                            signUp,
                            userRole,
                            email
                    )
                )
            );
    }

    /**
     * 회원가입된 유저 저장
     */
    @Transactional
    public void saveUser(UserAccount userAccount) {
        userAccountRepository.save(userAccount);
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(String email, String changedPassword) {
        UserAccount userAccount = userAccountRepository.getReferenceById(email);
        userAccount.setUserPassword(changedPassword);
    }
}
