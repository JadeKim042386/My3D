package joo.project.my3d.dto.security;

import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.UserAccountDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public record BoardPrincipal(
        String username,
        String password,
        Collection<? extends GrantedAuthority> authorities,
        String email,
        String nickname
        ) implements UserDetails {

    public static BoardPrincipal of(String username, String password, String email, String nickname, UserRole userRole) {

        return new BoardPrincipal(
                username,
                password,
                Set.of(new SimpleGrantedAuthority(userRole.getName())),
                email,
                nickname
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
