package joo.project.my3d.config;

import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.response.KakaoOauth2Response;
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
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.UUID;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService
    ) throws Exception {
        return http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .antMatchers(
                                "/css/**",
                                "/models/**",
                                "/node_modules/**",
                                "/oAuth-Buttons/**"
                        ).permitAll()
                        .regexMatchers(
                                HttpMethod.GET,
                                "/",
                                "/model_articles"
                        ).permitAll()
                        .regexMatchers(
                                "/model_articles/[0-9]+",
                                "/like/[0-9]+",
                                "/like/[0-9]+/delete",
                                "/comments/new",
                                "/comments/[0-9]+/delete"
                        ).authenticated()
                        .regexMatchers(
                                "/model_articles/form",
                                "/model_articles/form/[0-9]+",
                                "/model_articles/[0-9]+/delete"
                        ).hasAnyRole("COMPANY", "ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling()
                .and()
                    .formLogin(form -> form
                            .loginPage("/login")
                            .permitAll()
                    )
                    .logout(logout -> logout
                            .logoutUrl("/logout")
                    )
                    .oauth2Login(oAuth -> oAuth
                            .loginPage("/login")
                            .userInfoEndpoint(userInfo -> userInfo
                                    .userService(oAuth2UserService))
                    )
                .build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService(
            UserAccountService userAccountService,
            PasswordEncoder passwordEncoder
    ) {
        final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

        return userRequest -> {
            OAuth2User oAuth2User = delegate.loadUser(userRequest);
            KakaoOauth2Response kakaoResponse = KakaoOauth2Response.from(oAuth2User.getAttributes());
            String email = kakaoResponse.email();
            String dummyPassword = passwordEncoder.encode("{bcrypt}" + UUID.randomUUID());

            //회원이 존재하지 않는다면 해당 회원을 저장
            return userAccountService.searchUser(email)
                    .map(BoardPrincipal::from)
                    .orElseGet(() ->
                            BoardPrincipal.from(
                                    userAccountService.saveUser(
                                            email,
                                            dummyPassword,
                                            kakaoResponse.nickname(),
                                            UserRole.USER
                                    )
                            ));
        };
    }

    @Bean
    public UserDetailsService userDetailsService(UserAccountService userAccountService) {
        //loadUserByUsername
        return email -> userAccountService
                .searchUser(email)
                .map(BoardPrincipal::from)
                .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다. - email: " + email));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
