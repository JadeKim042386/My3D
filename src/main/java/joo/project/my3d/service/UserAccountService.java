package joo.project.my3d.service;

import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;

    public Optional<UserAccountDto> searchUser(String email) {
        return userAccountRepository.findById(email).map(UserAccountDto::from);
    }

    @Transactional
    public UserAccountDto saveUser(String email, String userPassword, String nickname, UserRole userRole) {
        return UserAccountDto.from(
                userAccountRepository.save(
                    UserAccount.of(
                            email,
                            userPassword,
                            nickname,
                            userRole
                    )
                )
            );
    }
}
