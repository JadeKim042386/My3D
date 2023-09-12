package joo.project.my3d.dto.response;

import joo.project.my3d.domain.constant.DimUnit;
import joo.project.my3d.dto.DimensionDto;

public record DimensionResponse(
        String dimName,
        Float dimValue,
        DimUnit dimUnit
) {
    public static DimensionResponse of(String dimName, Float dimValue, DimUnit dimUnit) {
        return new DimensionResponse(dimName, dimValue, dimUnit);
    }

    public static DimensionResponse from(DimensionDto dto) {
        return DimensionResponse.of(
                dto.dimName(),
                dto.dimValue(),
                dto.dimUnit()
        );
    }
}
