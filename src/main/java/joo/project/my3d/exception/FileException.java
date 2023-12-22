package joo.project.my3d.exception;

import lombok.Getter;

@Getter
public class FileException extends RuntimeException {

    private ErrorCode errorCode;


    public FileException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public FileException(ErrorCode errorCode, Exception causeException) {
        this.errorCode = errorCode;
        super.initCause(causeException);
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
