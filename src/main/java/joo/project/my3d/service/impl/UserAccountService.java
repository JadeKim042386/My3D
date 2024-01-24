package joo.project.my3d.service.impl;

import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.UserRefreshToken;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.dto.response.LoginResponse;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.exception.AuthException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.repository.UserAccountRepository;
import joo.project.my3d.repository.UserRefreshTokenRepository;
import joo.project.my3d.security.TokenProvider;
import joo.project.my3d.service.EmailServiceInterface;
import joo.project.my3d.service.UserAccountServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserAccountService implements UserAccountServiceInterface {

    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final UserAccountRepository userAccountRepository;
    private final EmailServiceInterface emailService;
    private final TokenProvider tokenProvider;
    private final BCryptPasswordEncoder encoder;

    @Override
    public UserAccountDto searchUser(String email) {
        return userAccountRepository.findByEmail(email)
                .map(UserAccountDto::from)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 유저입니다."));
    }

    @Override
    public boolean isExistsUserEmail(String email) {
        return userAccountRepository.existsByEmail(email);
    }

    @Override
    public boolean isExistsUserNickname(String nickname) {
        return userAccountRepository.existsByNickname(nickname);
    }

    /**
     * @throws UsernameNotFoundException 주어진 이메일에 해당하는 유저 정보를 DB에서 찾을 수 없을 경우 발생하는 예외
     */
    @Override
    public BoardPrincipal getUserPrincipal(String email) {
        return BoardPrincipal.from(searchUser(email));
    }

    /**
     * @throws AuthException 유저 정보 저장 실패시 발생하는 예외
     */
    @Transactional
    @Override
    public void saveUser(UserAccount userAccount) {
        try {
            userAccountRepository.save(userAccount);
        } catch (IllegalArgumentException e) {
            throw new AuthException(ErrorCode.FAILED_SAVE, e);
        } catch (OptimisticLockingFailureException e) {
            throw new AuthException(ErrorCode.CONFLICT_SAVE, e);
        }
    }

    @Transactional
    @Override
    public void changePassword(String email, String changedPassword) {
        UserAccount userAccount = userAccountRepository.getReferenceByEmail(email);
        userAccount.setUserPassword(encoder.encode(changedPassword));
    }

    @Transactional
    @Override
    public void sendTemporaryPassword(String email) {
        String code = String.valueOf(UUID.randomUUID()).split("-")[0];
        emailService.sendEmail(
                email,
                "[My3D] 이메일 임시 비밀번호",
                code
        );
        changePassword(email, code);
    }

    /**
     * @throws AuthException 유저 정보를 찾을 수 없을 경우 발생하는 예외
     */
    @Transactional
    @Override
    public void updateUser(UserAccountDto dto) {
        try {
            UserAccount userAccount = userAccountRepository.getReferenceByEmail(dto.email());
            if (dto.nickname() != null) {
                userAccount.setNickname(dto.nickname());
            }
            if (dto.phone() != null) {
                userAccount.setPhone(dto.phone());
            }
            if (dto.userPassword() != null) {
                userAccount.setUserPassword(dto.userPassword());
            }
            if (dto.addressDto() != null) {
                userAccount.setAddress(dto.addressDto().toEntity());
            }
        } catch (EntityNotFoundException e) {
            throw new AuthException(ErrorCode.INVALID_USER, e);
        }
    }

    /**
     * @throws AuthException 비밀번호가 일치하지 않거나 주어진 이메일에 해당하는 유저가 존재하지 않을 경우 발생하는 예외
     */
    @Transactional
    @Override
    public LoginResponse login(String email, String password) {
        UserAccountDto userAccountDto = searchUser(email);

        //비밀번호 일치 확인 (DB에 저장된 비밀번호는 encoded password)
        if (!encoder.matches(password, userAccountDto.userPassword())) {
            throw new AuthException(ErrorCode.INVALID_PASSWORD);
        }
        String accessToken = getAccessToken(email, userAccountDto.nickname(), userAccountDto);
        String refreshToken = tokenProvider.generateRefreshToken();
        updateRefreshToken(userAccountDto.id(), refreshToken);

        return LoginResponse.of(email, userAccountDto.nickname(), accessToken, refreshToken);
    }

    @Transactional
    @Override
    public LoginResponse oauthLogin(String email, String nickname) {
        UserAccountDto userAccountDto = searchUser(email);
        if (!nickname.equals(userAccountDto.nickname())) {
            throw new AuthException(ErrorCode.NOT_EQUAL_NICKNAME);
        }
        String accessToken = getAccessToken(email, nickname, userAccountDto);
        String refreshToken = tokenProvider.generateRefreshToken();
        updateRefreshToken(userAccountDto.id(), refreshToken);
        return LoginResponse.of(email, nickname, accessToken, refreshToken);
    }

    @Override
    public String getAccessToken(String email, String nickname, UserAccountDto userAccountDto) {

        return tokenProvider.generateAccessToken(
                email,
                nickname,
                String.format("%s:%s", userAccountDto.id(), userAccountDto.userRole())
        );
    }

    @Override
    public void updateRefreshToken(Long id, String refreshToken) {

        userRefreshTokenRepository.findById(id)
                .ifPresentOrElse(
                        it -> it.updateRefreshToken(refreshToken),
                        () -> userRefreshTokenRepository.save(UserRefreshToken.of(refreshToken))
                );
    }
}
