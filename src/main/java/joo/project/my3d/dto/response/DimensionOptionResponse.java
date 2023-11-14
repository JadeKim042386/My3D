package joo.project.my3d.dto.response;

import joo.project.my3d.dto.DimensionOptionWithDimensionDto;

import java.util.List;

public record DimensionOptionResponse(
        String optionName,
        List<DimensionResponse> dimensions
) {
    public static DimensionOptionResponse of(String optionName, List<DimensionResponse> dimensions) {
        return new DimensionOptionResponse(optionName, dimensions);
    }

    public static DimensionOptionResponse from(DimensionOptionWithDimensionDto dto) {
        return DimensionOptionResponse.of(
                dto.optionName(),
                dto.dimensionDtos().stream().map(DimensionResponse::from).toList()
        );
    }
}
