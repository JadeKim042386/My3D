package joo.project.my3d.exception;

import joo.project.my3d.exception.constant.ErrorCode;
import lombok.Getter;

@Getter
public class DimensionException extends CustomException {
    public DimensionException(ErrorCode errorCode) {
        super(errorCode);
    }

    public DimensionException(ErrorCode errorCode, Exception causeException) {
        super(errorCode, causeException);
    }
}
