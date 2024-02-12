package joo.project.my3d.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DisplayName("EmitterRepository 테스트")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = EmitterRepository.class)
class EmitterRepositoryTest {

    @Autowired
    private EmitterRepository emitterRepository;

    @DisplayName("Emitter 저장")
    @Test
    void saveEmitter() {
        // Given
        String email = "a@gmail.com";
        SseEmitter sseEmitter = new SseEmitter();
        // When
        SseEmitter savedSseEmitter = emitterRepository.save(email, sseEmitter);
        // Then
        assertThat(savedSseEmitter).isNotNull();
    }

    @DisplayName("Emitter 조회")
    @Test
    void getEmitter() {
        // Given
        String email = "a@gmail.com";
        // When
        Optional<SseEmitter> sseEmitter = emitterRepository.get(email);
        // Then
        assertThat(sseEmitter).isNotNull();
    }

    @DisplayName("Emitter 삭제")
    @Test
    void deleteEmitter() {
        // Given
        String email = "a@gmail.com";
        // When
        emitterRepository.delete(email);
        // Then
    }
}
