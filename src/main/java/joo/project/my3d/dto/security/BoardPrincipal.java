package joo.project.my3d.dto.security;

import joo.project.my3d.domain.Address;
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
        Long id,
        String email,
        String password,
        String phone,
        Collection<? extends GrantedAuthority> authorities,
        String nickname,
        Address address,
        Map<String, Object> oAuth2Attributes)
        implements UserDetails, OAuth2User {

    public static BoardPrincipal of(
            Long id,
            String email,
            String password,
            String phone,
            String nickname,
            UserRole userRole,
            Address address,
            Map<String, Object> oAuth2Attributes) {
        return new BoardPrincipal(
                id,
                email,
                password,
                phone,
                Set.of(new SimpleGrantedAuthority(userRole.getName())),
                nickname,
                address,
                oAuth2Attributes);
    }

    public static BoardPrincipal of(
            Long id, String email, String password, String phone, String nickname, UserRole userRole, Address address) {
        return BoardPrincipal.of(id, email, password, phone, nickname, userRole, address, Map.of());
    }

    /**
     * JWT Token to BoardPrincipal
     */
    public static BoardPrincipal of(Long id, String email, String nickname, UserRole userRole) {
        return BoardPrincipal.of(id, email, null, null, nickname, userRole, Address.of(null, null, null), Map.of());
    }

    /**
     * OAuth2UserService
     */
    public static BoardPrincipal of(String email, String password, String nickname, UserRole userRole) {
        return BoardPrincipal.of(
                null, email, password, null, nickname, userRole, Address.of(null, null, null), Map.of());
    }

    public static BoardPrincipal from(UserAccountDto dto) {
        return BoardPrincipal.of(
                dto.id(),
                dto.email(),
                dto.userPassword(),
                dto.phone(),
                dto.nickname(),
                dto.userRole(),
                dto.addressDto().toEntity());
    }

    public UserRole getUserRole() {
        return UserRole.valueOf(
                authorities.stream().toList().get(0).getAuthority().split("_")[1]);
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2Attributes;
    }

    @Override
    public String getName() {
        return email;
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
        return email;
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
