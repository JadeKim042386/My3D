package joo.project.my3d.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class ExceptionResponse {
    private String message;
    private Map<String, List<String>> errors;

    public static ExceptionResponse of(String message) {
        return new ExceptionResponse(message, null);
    }

    public static ExceptionResponse of(String message, Map<String, List<String>> errors) {
        return new ExceptionResponse(message, errors);
    }

    public static ExceptionResponse fromBindingResult(String message, BindingResult bindingResult) {
        return new ExceptionResponse(
                message,
                bindingResult.getFieldErrors().stream().collect(
                        Collectors.toMap(
                                FieldError::getField,
                                fieldError -> List.of(fieldError.getDefaultMessage())
                        )
                )
        );
    }
}
