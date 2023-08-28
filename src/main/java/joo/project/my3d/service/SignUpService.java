package joo.project.my3d.service;

import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.dto.security.BoardPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpService {

    /**
     * 직접 authentication 등록
     */
    public void setPrincipal(UserAccountDto dto) {
        BoardPrincipal principal = BoardPrincipal.from(dto);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        principal,
                        principal.password(),
                        principal.authorities()
                )
        );
    }
}
