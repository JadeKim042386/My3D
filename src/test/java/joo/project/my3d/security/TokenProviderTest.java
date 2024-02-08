package joo.project.my3d.security;

import com.fasterxml.jackson.core.JsonProcessingException;
import joo.project.my3d.domain.UserRefreshToken;
import joo.project.my3d.dto.properties.JwtProperties;
import joo.project.my3d.exception.AuthException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.repository.UserRefreshTokenRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@Disabled("테스트를 하려면 access token을 reissue")
@ActiveProfiles("test")
@EnableConfigurationProperties(JwtProperties.class)
@SpringBootTest(classes = TokenProvider.class)
class TokenProviderTest {
    @Autowired private TokenProvider tokenProvider;
    @MockBean private UserRefreshTokenRepository userRefreshTokenRepository;
    private final String ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6ImFAZ21haWwuY29tIiwibmlja25hbWUiOiJuaWNrbmFtZSIsInNwZWMiOiIxOlVTRVIiLCJpYXQiOjE3MDUwNDA1OTgsImV4cCI6MTcwNTA0MjM5OH0.fKMwnsagDghpt5gtxYEKzAjyYrfYomkmOuK6SwKaYRc";

    @DisplayName("access token 생성")
    @Test
    void generateAccessToken() {
        //given
        String email = "a@gmail.com";
        String nickname = "nickname";
        String spec = "1:USER";
        //when
        String accessToken = tokenProvider.generateAccessToken(email, nickname, spec);
        //then
        System.out.println(accessToken);
        assertThat(accessToken).isNotBlank();
    }

    @DisplayName("refresh token 생성")
    @Test
    void generateRefreshToken() {
        //given
        //when
        String refreshToken = tokenProvider.generateRefreshToken();
        //then
        System.out.println(refreshToken);
        assertThat(refreshToken).isNotBlank();
    }

    @DisplayName("access token 재발행")
    @Test
    void regenerateAccessToken() throws JsonProcessingException {
        //given
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MDUwMzg4MDUsImV4cCI6MTcwNTEyNTIwNX0.LOdbNm68s1FxvEoibKOTpQCj_Ball2gBjAQiWl3yw5s";
        given(userRefreshTokenRepository.findByUserAccountIdAndReissueCountLessThan(anyLong(), anyLong()))
                .willReturn(Optional.of(UserRefreshToken.of(refreshToken)));
        //when
        String accessToken = tokenProvider.regenerateAccessToken(ACCESS_TOKEN);
        //then
        assertThat(accessToken).isNotBlank();
    }

    @DisplayName("access token 재발행 - 재발행 횟수 제한")
    @Test
    void regenerateAccessToken_exceedReissue() {
        //given
        given(userRefreshTokenRepository.findByUserAccountIdAndReissueCountLessThan(anyLong(), anyLong()))
                .willThrow(new AuthException(ErrorCode.EXCEED_REISSUE));
        //when
        assertThatThrownBy(() -> tokenProvider.regenerateAccessToken(ACCESS_TOKEN))
                .isInstanceOf(AuthException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EXCEED_REISSUE)
        ;
        //then
    }

    @DisplayName("refresh token validation")
    @Test
    void validateRefreshToken() {
        //given
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MDUwMzg4MDUsImV4cCI6MTcwNTEyNTIwNX0.LOdbNm68s1FxvEoibKOTpQCj_Ball2gBjAQiWl3yw5s";
        given(userRefreshTokenRepository.findByUserAccountIdAndReissueCountLessThan(anyLong(), anyLong()))
                .willReturn(Optional.of(UserRefreshToken.of(refreshToken)));
        //when
        assertThatNoException()
                .isThrownBy(() -> tokenProvider.validateRefreshToken(refreshToken, ACCESS_TOKEN));
        //then
    }

    @DisplayName("refresh token validation - DB에 있는 refresh token과 일치하지 않을 경우")
    @Test
    void validateRefreshToken_notEqualRefreshToken() {
        //given
        String refreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MDUwMzg4MDUsImV4cCI6MTcwNTEyNTIwNX0.LOdbNm68s1FxvEoibKOTpQCj_Ball2gBjAQiWl3yw5s";
        given(userRefreshTokenRepository.findByUserAccountIdAndReissueCountLessThan(anyLong(), anyLong()))
                .willThrow(new AuthException(ErrorCode.NOT_EQUAL_TOKEN));
        //when
        assertThatThrownBy(() -> tokenProvider.validateRefreshToken(refreshToken, ACCESS_TOKEN))
                .isInstanceOf(AuthException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_EQUAL_TOKEN);
        //then
    }
}