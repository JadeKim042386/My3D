package joo.project.my3d.security;

import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.UserRefreshToken;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.UserAccountDto;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
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
            JwtTokenFilter jwtTokenFilter,
            CustomOAuth2SuccessHandler customOAuth2SuccessHandler
    ) throws Exception {
        return http
                .httpBasic().disable()
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
                                "/account/oauth/response",
                                "/account/login",
                                "/account/signup",
                                "/account/signup/duplicatedCheck",
                                "/account/find_pass",
                                "/account/find_pass/success",
                                "/account/type",
                                "/account/company",
                                "/mail/send_code",
                                "/mail/find_pass"
                        ).hasRole("ANONYMOUS")
                        .regexMatchers(
                                "/model_articles/[0-9]+",
                                "/like/[0-9]+",
                                "/comments.*",
                                "/user/account",
                                "/user/password",
                                "/user/alarm.*",
                                "/model_articles/add",
                                "/model_articles/update/[0-9]+",
                                "/model_articles/download/[0-9]+"
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
                    )
                    .oauth2Login(oAuth -> oAuth
                            .loginPage("/oauth")
                            .userInfoEndpoint(userInfo -> userInfo
                                    .userService(oAuth2UserService))
                            .successHandler(customOAuth2SuccessHandler) //OAuth 로그인 후 cookie에 jwt 토큰 저장
                    )
                    //cookie에서 token을 가져와 authentication 등록
                    .addFilterBefore(
                            jwtTokenFilter,
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
            try {
                UserAccountDto userAccountDto = userAccountService.searchUser(email);
                return BoardPrincipal.from(userAccountDto);
            } catch (UsernameNotFoundException e) {
                return BoardPrincipal.from(
                        // TODO: dto로 변환하는 과정은 불필요해 보임
                        UserAccountDto.from(
                                UserAccount.of(
                                        email,
                                        dummyPassword,
                                        attributes.getName(),
                                        UserRole.ANONYMOUS,
                                        UserRefreshToken.of(null)
                                )
                        )
                );
            }
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

    @Bean
    public SecurityContextLogoutHandler securityContextLogoutHandler() {
        return new SecurityContextLogoutHandler();
    }
}
