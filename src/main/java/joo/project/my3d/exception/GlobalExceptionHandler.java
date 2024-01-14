package joo.project.my3d.exception;

import joo.project.my3d.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;

import static joo.project.my3d.exception.constant.ErrorCode.INTERNAL_SERVER_ERROR_CODE;
import static joo.project.my3d.exception.constant.ErrorCode.INVALID_REQUEST;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<String> handleMethodArgsException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException is occurred.", e);
        return ApiResponse.error(INVALID_REQUEST.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ApiResponse<String> handleDataViolationException(DataIntegrityViolationException e) {
        log.error("DataIntegrityViolationException is occurred.", e);
        return ApiResponse.error(INVALID_REQUEST.getMessage());
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleAllException(Exception e) {
        log.error("Exception is occurred.", e);
        return ApiResponse.error(INTERNAL_SERVER_ERROR_CODE.getMessage());
    }

    @ExceptionHandler(value = CustomException.class)
    public ApiResponse<String> handleModelArticlesException(HttpServletResponse response, CustomException e) {
        response.setStatus(e.getErrorCode().getStatus().value());
        return ApiResponse.error(e.getMessage());
    }
}
