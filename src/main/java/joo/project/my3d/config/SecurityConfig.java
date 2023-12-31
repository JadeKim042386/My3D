package joo.project.my3d.config;

import joo.project.my3d.config.filter.JwtTokenFilter;
import joo.project.my3d.config.handler.CustomOAuth2SuccessHandler;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.dto.properties.JwtProperties;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.dto.security.OAuthAttributes;
import joo.project.my3d.service.UserAccountService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

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
            OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService,
            UserAccountService userAccountService,
            JwtProperties jwtProperties,
            CustomOAuth2SuccessHandler customOAuth2SuccessHandler
    ) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .mvcMatchers(
                                "/",
                                "/model_articles",
                                "/guide/materials",
                                "/guide/printing_process",
                                "/profile",
                                "/account/logout"
                        ).permitAll()
                        .mvcMatchers(
                                "/account/login",
                                "/account/sign_up",
                                "/account/find_pass",
                                "/account/find_pass_success",
                                "/account/type",
                                "/account/company",
                                "/mail/send_code",
                                "/mail/find_pass"
                        ).hasRole("ANONYMOUS")
                        .regexMatchers(
                                "/model_articles/[0-9]+",
                                "/like/[0-9]+.*",
                                "/comments.*",
                                "/user/account",
                                "/user/password",
                                "/user/alarm.*",
                                "/model_articles/form.*",
                                "/model_articles/[0-9]+/delete"
                        ).authenticated()
                        .regexMatchers("/user/company")
                            .hasAnyRole("COMPANY", "ADMIN")
                        .anyRequest().authenticated()
                )
                .csrf((csrf) -> csrf
                        .csrfTokenRepository(new CookieCsrfTokenRepository())
                )
                .exceptionHandling()
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //세션이 아닌 JWT를 이용하여 인증 진행
                .and()
                    .formLogin().disable()
                    .logout(logout -> logout
                            .logoutUrl("/account/logout")
                            .deleteCookies(jwtProperties.cookieName())
                    )
                    .oauth2Login(oAuth -> oAuth
                            .loginPage("/account/login")
                            .userInfoEndpoint(userInfo -> userInfo
                                    .userService(oAuth2UserService))
                            .successHandler(customOAuth2SuccessHandler) //OAuth 로그인 후 cookie에 jwt 토큰 저장
                    )
                    //cookie에서 token을 가져와 authentication 등록
                    .addFilterBefore(
                            new JwtTokenFilter(userAccountService, jwtProperties),
                            ExceptionTranslationFilter.class)
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
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
            OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
            String email = attributes.getEmail();
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
                                                    attributes.getName(),
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
        return userAccountService::getUserPrincipal;
    }

    @Bean
    public BCryptPasswordEncoder encoderPassword() {
        return new BCryptPasswordEncoder();
    }
}
