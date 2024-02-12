package joo.project.my3d.dto.response;

public record EmailResponse(String email, String emailCode) {
    public static EmailResponse sendSuccess(String email, String emailCode) {
        return new EmailResponse(email, emailCode);
    }
}
