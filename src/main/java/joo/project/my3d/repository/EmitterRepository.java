package joo.project.my3d.repository;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Repository
@NoArgsConstructor
public class EmitterRepository {

    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    public SseEmitter save(String email, SseEmitter sseEmitter) {
        final String key = getKey(email);
        emitterMap.put(key, sseEmitter);
        return sseEmitter;
    }

    public Optional<SseEmitter> get(String email) {
        final String key = getKey(email);
        return Optional.ofNullable(emitterMap.get(key));
    }

    public void delete(String email) {
        emitterMap.remove(getKey(email));
    }

    private String getKey(String email) {
        return "Emitter:Email:" + email;
    }
}
