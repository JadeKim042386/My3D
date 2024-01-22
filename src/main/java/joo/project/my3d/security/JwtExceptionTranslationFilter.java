package joo.project.my3d.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import joo.project.my3d.dto.response.ExceptionResponse;
import joo.project.my3d.exception.AuthException;
import joo.project.my3d.exception.constant.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtExceptionTranslationFilter extends OncePerRequestFilter {
    private final ObjectMapper mapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            chain.doFilter(request, response);
        } catch (AuthException e) {
            writeExceptionResponse(response, e.getErrorCode());
        }
    }

    private void writeExceptionResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
        response.setStatus(errorCode.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            response.getWriter().write(mapper.writeValueAsString(
                    ExceptionResponse.of(errorCode.getMessage())
            ));
        } catch (IOException e) {
            log.error("Failed write ExceptionResponse.");
            throw e;
        }
    }
}
