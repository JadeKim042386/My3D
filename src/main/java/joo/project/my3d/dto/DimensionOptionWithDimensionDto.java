package joo.project.my3d.dto;

import joo.project.my3d.domain.DimensionOption;

import java.util.List;

public record DimensionOptionWithDimensionDto(String optionName, List<DimensionDto> dimensions) {
    public static DimensionOptionWithDimensionDto of(String optionName, List<DimensionDto> dimensionDtos) {
        return new DimensionOptionWithDimensionDto(optionName, dimensionDtos);
    }

    public static DimensionOptionWithDimensionDto from(DimensionOption dimensionOption) {
        return DimensionOptionWithDimensionDto.of(
                dimensionOption.getOptionName(),
                dimensionOption.getDimensions().stream().map(DimensionDto::from).toList());
    }

    public DimensionOption toEntity() {
        DimensionOption dimensionOption = DimensionOption.of(optionName);
        dimensionOption
                .getDimensions()
                .addAll(dimensions.stream()
                        .map(dto -> dto.toEntity(dimensionOption))
                        .toList());
        return dimensionOption;
    }
}
