package joo.project.my3d.exception;

import lombok.Getter;

@Getter
public class DimensionException extends RuntimeException {

    private ErrorCode errorCode;

    public DimensionException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public DimensionException(ErrorCode errorCode, Exception causeException) {
        this.errorCode = errorCode;
        super.initCause(causeException);
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
