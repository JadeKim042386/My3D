package joo.project.my3d.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ArticleException extends RuntimeException {

    private ErrorCode errorCode;
    private Exception parentException;
    private String message;

    public ArticleException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ArticleException(ErrorCode errorCode, Exception parentException) {
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
