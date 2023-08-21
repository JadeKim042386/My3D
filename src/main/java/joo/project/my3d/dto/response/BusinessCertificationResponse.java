package joo.project.my3d.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record BusinessCertificationResponse(
        @JsonProperty("data") List<Data> data
) {
    public static BusinessCertificationResponse empty() {
        return new BusinessCertificationResponse(
                List.of(
                        new Data(
                        null,
                        null,
                        null)
                )
        );
    }

    public record Data(
            String b_no,
            String b_stt_cd,
            String tax_type_cd
    ) {}
}
