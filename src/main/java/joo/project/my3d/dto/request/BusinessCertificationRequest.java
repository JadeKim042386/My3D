package joo.project.my3d.dto.request;

import javax.validation.constraints.Pattern;

public record BusinessCertificationRequest(
        @Pattern(regexp = "(?=.*[0-9]).{10,10}", message = "사업자 등록번호는 숫자 10자리입니다")
        String b_no,
        String b_stt_cd
) {
}
