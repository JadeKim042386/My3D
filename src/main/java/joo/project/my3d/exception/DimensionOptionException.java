package joo.project.my3d.exception;

import joo.project.my3d.exception.constant.ErrorCode;
import lombok.Getter;

@Getter
public class DimensionOptionException extends CustomException {
    public DimensionOptionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DimensionOptionException(ErrorCode errorCode, Exception causeException) {
        super(errorCode, causeException);
    }
}
