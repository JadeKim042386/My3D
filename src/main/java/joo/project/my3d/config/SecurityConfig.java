package joo.project.my3d.config;

import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.dto.response.KakaoOauth2Response;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.service.UserAccountService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

import java.util.UUID;

@Configuration
public class SecurityConfig {

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(
                "/static/**",
                "/models/**",
                "/node_modules/**",
                "/oAuth-Buttons/**",
                "/js/**",
                "/css/**"
        );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService
    ) throws Exception {
        return http
                .csrf().disable()
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .mvcMatchers(
                                HttpMethod.GET,
                                "/",
                                "/model_articles"
                        ).permitAll()
                        .mvcMatchers(
                                "/account/login",
                                "/account/sign_up",
                                "/account/find_pass",
                                "/account/type",
                                "/account/company"
                        ).hasRole("ANONYMOUS")
                        .regexMatchers(
                                "/account/logout",
                                "/model_articles/[0-9]+",
                                "/like/[0-9]+.*",
                                "/comments.*"
                        ).authenticated()
                        .regexMatchers(
                                "/model_articles/form.*",
                                "/model_articles/[0-9]+/delete"
                        ).hasAnyRole("COMPANY", "ADMIN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling()
                .and()
                    .formLogin(form -> form
                            .loginPage("/account/login")
                            .usernameParameter("email")
                    )
                    .logout(logout -> logout
                            .logoutUrl("/account/logout")
                            .logoutSuccessUrl("/")
                    )
                    .oauth2Login(oAuth -> oAuth
                            .loginPage("/account/login")
                            .userInfoEndpoint(userInfo -> userInfo
                                    .userService(oAuth2UserService))
                            .defaultSuccessUrl("/account/type", true)
                    )
                    .addFilterAfter(
                            new CustomOAuth2LoginAuthenticationFilter(),
                            OAuth2LoginAuthenticationFilter.class
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

            //회원이 존재하지 않는다면 저장하지 않고 회원가입이 안된 상태로 반환
            return userAccountService.searchUser(email)
                    .map(BoardPrincipal::from)
                    .orElseGet(() ->
                            BoardPrincipal.from(
                                    UserAccountDto.from(
                                            UserAccount.of(
                                                    email,
                                                    dummyPassword,
                                                    kakaoResponse.nickname(),
                                                    false,
                                                    UserRole.ANONYMOUS,
                                                    email
                                            )
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
