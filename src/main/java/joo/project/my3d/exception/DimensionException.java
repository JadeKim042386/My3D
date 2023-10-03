package joo.project.my3d.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DimensionException extends RuntimeException {

    private ErrorCode errorCode;
    private Exception parentException;
    private String message;


    public DimensionException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public DimensionException(ErrorCode errorCode, Exception parentException) {
        this.errorCode = errorCode;
        this.parentException = parentException;
    }

    @Override
    public String getMessage() {
        if (message == null) {
            return errorCode.getMessage();
        }

        if (parentException == null) {
            return String.format("%s. %s", errorCode.getMessage(), message);
        }

        return String.format("%s. %s. %s", errorCode.getMessage(), message, parentException.getMessage());
    }
}
