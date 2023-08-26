package joo.project.my3d.service;

import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.dto.properties.JwtProperties;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.exception.UserAccountException;
import joo.project.my3d.repository.UserAccountRepository;
import joo.project.my3d.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtProperties jwtProperties;

    public List<UserAccountDto> findAllUser() {
        return userAccountRepository.findAll().stream()
                .map(UserAccountDto::from).toList();
    }

    public Optional<UserAccountDto> searchUser(String email) {
        return userAccountRepository.findById(email).map(UserAccountDto::from);
    }

    public BoardPrincipal getUserPrincipal(String email) {
        return searchUser(email)
                .map(BoardPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다. - email: " + email));
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

    /**
     * 비밀번호 일치 확인 후 토큰 생성
     */
    public String login(String email, String password) {
        BoardPrincipal principal = getUserPrincipal(email);

        //비밀번호 일치 확인
        if (!encoder.matches(password, principal.getPassword())) {
            throw new UserAccountException(ErrorCode.INVALID_PASSWORD);
        }

        //토큰 생성 후 반환
        return JwtTokenUtils.generateToken(
                email,
                principal.nickname(),
                jwtProperties.secretKey(),
                jwtProperties.expiredTimeMs()
        );
    }
}
