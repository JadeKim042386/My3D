package joo.project.my3d.service;

import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.dto.response.LoginResponse;
import joo.project.my3d.dto.security.BoardPrincipal;

public interface UserAccountServiceInterface {
    /**
     * 유저 정보 조회
     */
    UserAccountDto searchUser(String email);

    /**
     * 이메일 또는 닉네임 존재 유무 조회
     */
    boolean isExistsUserEmailOrNickname(String email, String nickname);
    boolean isExistsUserEmailOrNickname(String email);

    /**
     * 유저 정보를 조회하여 BoardPrincipal 객체로 반환
     */
    BoardPrincipal getUserPrincipal(String email);

    /**
     * 유저 저장
     */
    void saveUser(UserAccount userAccount);

    /**
     * 비밀번호 변경
     */
    void changePassword(String email, String changedPassword);
    /**
     * 임시 비밀번호 발급
     */
    String sendTemporaryPassword(String email);
    /**
     * 유저 정보 수정
     */
    void updateUser(UserAccountDto dto);

    /**
     * 로그인
     */
    LoginResponse login(String email, String password);

    /**
     * OAuth 로그인
     */
    LoginResponse oauthLogin(String email, String nickname);
    /**
     * Access Token 발급
     */
    String getAccessToken(String email, String nickname, UserAccountDto userAccountDto);

    /**
     * Refresh Token 업데이트 (이미 존재할 경우 업데이트하고 없다면 저장)
     */
    void updateRefreshToken(Long id, String refreshToken);

    /**
     * Validate Token (토큰이 유효하지 않다면 예외 발생)과 Role 반환
     */
    String getRoleFromToken(String token);
}
