package joo.project.my3d.dto.security;

import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.UserAccountDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public record BoardPrincipal(
        String username,
        String password,
        Collection<? extends GrantedAuthority> authorities,
        String email,
        String nickname,
        Map<String, Object> oAuth2Attributes
        ) implements UserDetails, OAuth2User {

    public static BoardPrincipal of(String username, String password, String email, String nickname, UserRole userRole) {

        return new BoardPrincipal(
                username,
                password,
                Set.of(new SimpleGrantedAuthority(userRole.getName())),
                email,
                nickname,
                Map.of()
        );
    }

    public static BoardPrincipal of(String username, String password, String email, String nickname, UserRole userRole, Map<String, Object> oAuth2Attributes) {

        return new BoardPrincipal(
                username,
                password,
                Set.of(new SimpleGrantedAuthority(userRole.getName())),
                email,
                nickname,
                oAuth2Attributes
        );
    }

    public static BoardPrincipal from(UserAccountDto dto) {
        return BoardPrincipal.of(
                dto.userId(),
                dto.userPassword(),
                dto.email(),
                dto.nickname(),
                dto.userRole()
        );
    }

    public UserAccountDto toDto() {
        return UserAccountDto.of(
              username,
              password,
              email,
              nickname
        );
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2Attributes;
    }
    @Override
    public String getName() {
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}
