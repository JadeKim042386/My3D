package joo.project.my3d.config;

import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.repository.UserAccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .antMatchers(
                                "/css/**",
                                "/models/**",
                                "/node_modules/**"
                        ).permitAll()
                        .mvcMatchers(
                                HttpMethod.GET,
                                "/",
                                "/model_articles"
                        ).permitAll()
                        .regexMatchers(
                                HttpMethod.GET,
                                "/model_articles/[0-9]+"
                        ).permitAll()
                        .mvcMatchers("/model_articles/form").hasAnyRole("COMPANY, ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling()
//                .and()
//                    .sessionManagement()
//                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //세션을 생성하지 않음
                .and()
                    .formLogin(withDefaults())
                    .logout(logout -> logout.logoutSuccessUrl("/"))
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserAccountRepository userAccountRepository) {
        //loadUserByUsername
        return username -> userAccountRepository
                .findById(username)
                .map(UserAccountDto::from)
                .map(BoardPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다. - username: " + username));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
