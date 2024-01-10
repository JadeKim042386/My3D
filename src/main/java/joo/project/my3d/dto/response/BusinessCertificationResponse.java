package joo.project.my3d.dto.response;

public record BusinessCertificationResponse(
        String b_no,
        String b_stt_cd,
        String serviceKey
) {
    public static BusinessCertificationResponse of(String b_no, String b_stt_cd, String serviceKey) {
        return new BusinessCertificationResponse(b_no, b_stt_cd, serviceKey);
    }
}
