package joo.project.my3d.dto.response;

import joo.project.my3d.dto.GoodOptionWithDimensionDto;

import java.util.List;

public record GoodOptionResponse(
        String optionName,
        Integer addPrice,
        String printingTech,
        String material,
        List<DimensionResponse> dimensions
) {
    public static GoodOptionResponse of(String optionName, Integer addPrice, String printingTech, String material, List<DimensionResponse> dimensions) {
        return new GoodOptionResponse(optionName, addPrice, printingTech, material, dimensions);
    }

    public static GoodOptionResponse from(GoodOptionWithDimensionDto dto) {
        return GoodOptionResponse.of(
                dto.optionName(),
                dto.addPrice(),
                dto.printingTech(),
                dto.material(),
                dto.dimensionDtos().stream().map(DimensionResponse::from).toList()
        );
    }
}
