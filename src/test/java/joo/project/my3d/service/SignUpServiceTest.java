package joo.project.my3d.service;

import joo.project.my3d.config.TestSecurityConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;

import static org.assertj.core.api.Assertions.*;

@DisplayName("비지니스 로직 - 회원가입")
@Import(TestSecurityConfig.class)
@SpringBootTest
class SignUpServiceTest {

    @Autowired private SignUpService signUpService;

    @DisplayName("국세청 API를 활용한 사업자 인증 - 상태 코드 반환")
    @WithAnonymousUser
    @Test
    void businessCertification() {
        // Given

        // When
        String b_stt_cd = signUpService.businessCertification("2208162517"); //네이버
        // Then
        assertThat(b_stt_cd).isEqualTo("01");
    }
}