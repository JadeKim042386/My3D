package joo.project.my3d.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ExceptionResponse {
    private String message;
    private List<String> errors;

    public static ExceptionResponse of(String message) {
        return new ExceptionResponse(message, null);
    }

    public static ExceptionResponse of(String message, List<String> errors) {
        return new ExceptionResponse(message, errors);
    }
}
