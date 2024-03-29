package joo.project.my3d.exception;

import joo.project.my3d.dto.response.ExceptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> resolveException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException is occurred.", e);
        return ResponseEntity.status(BAD_REQUEST).body(ExceptionResponse.of(e.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionResponse> resolveException(DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException is occurred.", e);
        return ResponseEntity.status(BAD_REQUEST).body(ExceptionResponse.of(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> resolveException(Exception e) {
        log.error("Exception is occurred.", e);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(ExceptionResponse.of(e.getMessage()));
    }

    @ExceptionHandler(value = ValidatedException.class)
    public ResponseEntity<ExceptionResponse> resolveException(ValidatedException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus()).body(e.getExceptionResponse());
    }

    @ExceptionHandler(value = CustomException.class)
    public ResponseEntity<ExceptionResponse> resolveException(CustomException e) {
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(ExceptionResponse.of(e.getErrorCode().getMessage()));
    }
}
