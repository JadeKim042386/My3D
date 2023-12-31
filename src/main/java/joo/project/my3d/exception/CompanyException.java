package joo.project.my3d.exception;

import lombok.Getter;

@Getter
public class CompanyException extends RuntimeException {

    private ErrorCode errorCode;


    public CompanyException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public CompanyException(ErrorCode errorCode, Exception causeException) {
        this.errorCode = errorCode;
        super.initCause(causeException);
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }
}
