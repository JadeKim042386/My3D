package joo.project.my3d.dto.response;

import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.exception.constant.ErrorCode;

public record EmailResponse(
        String email,
        ErrorCode errorCode,
        String emailCode,
        UserRole userRole
) {
    public static EmailResponse sendError(String email, ErrorCode errorCode, UserRole userRole) {
        return new EmailResponse(email, errorCode, null, userRole);
    }

    public static EmailResponse sendError(String email, ErrorCode errorCode) {
        return EmailResponse.sendError(email, errorCode, null);
    }

    public static EmailResponse sendSuccess(String email, String emailCode, UserRole userRole) {
        return new EmailResponse(email, null, emailCode, userRole);
    }

    public static EmailResponse sendSuccess(String email) {
        return EmailResponse.sendSuccess(email, null, null);
    }
}
