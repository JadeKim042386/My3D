package joo.project.my3d.service;

import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.AlarmDto;
import joo.project.my3d.dto.CompanyDto;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.dto.properties.JwtProperties;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.exception.UserAccountException;
import joo.project.my3d.repository.AlarmRepository;
import joo.project.my3d.repository.UserAccountRepository;
import joo.project.my3d.utils.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final AlarmRepository alarmRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtProperties jwtProperties;

    public List<UserAccountDto> findAllUser() {
        return userAccountRepository.findAll().stream()
                .map(UserAccountDto::from).toList();
    }

    public Optional<UserAccountDto> searchUser(String email) {
        return userAccountRepository.findByEmail(email).map(UserAccountDto::from);
    }

    /**
     * @throws UsernameNotFoundException 유저가 존재하지 않는 경우 발생하는 예외
     */
    public CompanyDto getCompany(String email) {
        return searchUser(email)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다. - email: " + email))
                .companyDto();
    }

    /**
     * DB로부터 유저 정보를 가져와 BoardPrincipal 객체로 변환한다.
     * @throws UsernameNotFoundException 주어진 이메일에 해당하는 유저 정보를 DB에서 찾을 수 없을 경우 발생하는 예외
     */
    public BoardPrincipal getUserPrincipal(String email) {
        return searchUser(email)
                .map(BoardPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다. - email: " + email));
    }

    public List<AlarmDto> getAlarms(String email) {
        return alarmRepository.findAllByUserAccount_Email(email).stream()
                .map(AlarmDto::from)
                .sorted(Comparator.comparing(AlarmDto::createdAt).reversed())
                .toList();
    }

    /**
     * @throws UserAccountException 유저 정보 저장 실패시 발생하는 예외
     */
    @Transactional
    public UserAccountDto saveUser(String email, String userPassword, String nickname, UserRole userRole, boolean signUp) {
        try {
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
        } catch (IllegalArgumentException e) {
            throw new UserAccountException(ErrorCode.FAILED_SAVE, e);
        } catch (OptimisticLockingFailureException e) {
            throw new UserAccountException(ErrorCode.CONFLICT_SAVE, e);
        }
    }

    /**
     * 회원가입된 유저 저장
     * @throws UserAccountException 유저 정보 저장 실패시 발생하는 예외
     */
    @Transactional
    public void saveUser(UserAccount userAccount) {
        try {
            userAccountRepository.save(userAccount);
        } catch (IllegalArgumentException e) {
            throw new UserAccountException(ErrorCode.FAILED_SAVE, e);
        } catch (OptimisticLockingFailureException e) {
            throw new UserAccountException(ErrorCode.CONFLICT_SAVE, e);
        }
    }

    /**
     * 비밀번호 변경
     */
    @Transactional
    public void changePassword(String email, String changedPassword) {
        UserAccount userAccount = userAccountRepository.getReferenceByEmail(email);
        userAccount.setUserPassword(encoder.encode(changedPassword));
    }

    /**
     * 계정 정보 수정
     * @throws UserAccountException 유저 정보를 찾을 수 없을 경우 발생하는 예외
     */
    @Transactional
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
            throw new UserAccountException(ErrorCode.INVALID_USER, e);
        }
    }

    /**
     * 비밀번호 일치 확인 후 토큰을 생성하여 반환한다.
     * @throws UserAccountException 비밀번호가 일치하지 않거나 주어진 이메일에 해당하는 유저가 존재하지 않을 경우 발생하는 예외
     */
    public String login(String email, String password) {
        try {
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
        } catch (UsernameNotFoundException e) {
            throw new UserAccountException(ErrorCode.INVALID_USER, e);
        }
    }
}
