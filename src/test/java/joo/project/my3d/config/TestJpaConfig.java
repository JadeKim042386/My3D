package joo.project.my3d.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@TestConfiguration
@EnableJpaAuditing
public class TestJpaConfig {
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("joo");
    }
}
