package joo.project.my3d.dto.response;

import joo.project.my3d.exception.constant.ErrorCode;

public record EmailResponse(
        String email,
        ErrorCode errorCode,
        String emailCode
) {
    public static EmailResponse sendError(String email, ErrorCode errorCode) {
        return new EmailResponse(email, errorCode, null);
    }

    public static EmailResponse sendSuccess(String email, String emailCode) {
        return new EmailResponse(email, null, emailCode);
    }

    public static EmailResponse sendSuccess(String email) {
        return EmailResponse.sendSuccess(email, null);
    }
}
