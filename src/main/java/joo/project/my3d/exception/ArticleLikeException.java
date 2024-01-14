package joo.project.my3d.exception;

import joo.project.my3d.exception.constant.ErrorCode;
import lombok.Getter;

@Getter
public class ArticleLikeException extends CustomException {
    public ArticleLikeException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ArticleLikeException(ErrorCode errorCode, Exception causeException) {
        super(errorCode, causeException);
    }
}
