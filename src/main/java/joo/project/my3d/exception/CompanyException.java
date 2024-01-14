package joo.project.my3d.exception;

import joo.project.my3d.exception.constant.ErrorCode;
import lombok.Getter;

@Getter
public class CompanyException extends CustomException {
    public CompanyException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CompanyException(ErrorCode errorCode, Exception causeException) {
        super(errorCode, causeException);
    }
}
