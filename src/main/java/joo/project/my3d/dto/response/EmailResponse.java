package joo.project.my3d.dto.response;

import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.exception.constant.MailErrorCode;

public record EmailResponse(
        String email,
        MailErrorCode emailError,
        String emailCode,
        UserRole userRole
) {
    public static EmailResponse sendError(String email, MailErrorCode emailError, UserRole userRole) {
        return new EmailResponse(email, emailError, null, userRole);
    }

    public static EmailResponse sendError(String email, MailErrorCode emailError) {
        return EmailResponse.sendError(email, emailError, null);
    }

    public static EmailResponse sendSuccess(String email, String emailCode, UserRole userRole) {
        return new EmailResponse(email, null, emailCode, userRole);
    }

    public static EmailResponse sendSuccess(String email) {
        return EmailResponse.sendSuccess(email, null, null);
    }
}
