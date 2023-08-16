package joo.project.my3d.config;

import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.service.UserAccountService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
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
                        .regexMatchers(
                                HttpMethod.GET,
                                "/",
                                "/model_articles"
                        ).permitAll()
                        .regexMatchers(
                                "/model_articles/[0-9]+",
                                "/like/[0-9]+",
                                "/like/[0-9]+/delete"
                        ).authenticated()
                        .regexMatchers(
                                "/model_articles/form",
                                "/model_articles/form/[0-9]+",
                                "/model_articles/[0-9]+/delete"
                        ).hasAnyRole("COMPANY", "ADMIN")
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
    public UserDetailsService userDetailsService(UserAccountService userAccountService) {
        //loadUserByUsername
        return username -> userAccountService
                .searchUser(username)
                .map(BoardPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다. - username: " + username));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
