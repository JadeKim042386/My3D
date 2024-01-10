package joo.project.my3d.dto.response;

public record EmailResponse(
        String email,
        String emailError
) {
    public static EmailResponse of(String email, String emailError) {
        return new EmailResponse(email, emailError);
    }
}
