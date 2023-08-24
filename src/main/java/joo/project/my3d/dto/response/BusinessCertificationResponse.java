package joo.project.my3d.dto.response;

public record BusinessCertificationResponse(
        String b_no,
        String b_stt_cd
) {
    public static BusinessCertificationResponse of(String b_no, String b_stt_cd) {
        return new BusinessCertificationResponse(b_no, b_stt_cd);
    }
}
