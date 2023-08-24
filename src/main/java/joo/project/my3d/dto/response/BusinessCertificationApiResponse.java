package joo.project.my3d.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record BusinessCertificationApiResponse(
        @JsonProperty("data") List<Data> data
) {
    public static BusinessCertificationApiResponse empty() {
        return new BusinessCertificationApiResponse(
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
