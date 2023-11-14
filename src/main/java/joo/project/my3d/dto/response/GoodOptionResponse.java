package joo.project.my3d.dto.response;

import joo.project.my3d.dto.GoodOptionWithDimensionDto;

import java.util.List;

public record GoodOptionResponse(
        String optionName,
        String printingTech,
        String material,
        List<DimensionResponse> dimensions
) {
    public static GoodOptionResponse of(String optionName, String printingTech, String material, List<DimensionResponse> dimensions) {
        return new GoodOptionResponse(optionName, printingTech, material, dimensions);
    }

    public static GoodOptionResponse from(GoodOptionWithDimensionDto dto) {
        return GoodOptionResponse.of(
                dto.optionName(),
                dto.printingTech(),
                dto.material(),
                dto.dimensionDtos().stream().map(DimensionResponse::from).toList()
        );
    }
}
