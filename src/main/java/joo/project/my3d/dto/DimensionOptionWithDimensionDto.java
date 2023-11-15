package joo.project.my3d.dto;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.DimensionOption;

import java.util.List;

public record DimensionOptionWithDimensionDto(
        String optionName,
        List<DimensionDto> dimensionDtos
) {
    public static DimensionOptionWithDimensionDto of(String optionName, List<DimensionDto> dimensionDtos) {
        return new DimensionOptionWithDimensionDto(optionName, dimensionDtos);
    }

    public DimensionOption toEntity(Article article) {
        return DimensionOption.of(
                article,
                optionName
        );
    }

    public static DimensionOptionWithDimensionDto from(DimensionOption dimensionOption) {
        return DimensionOptionWithDimensionDto.of(
                dimensionOption.getOptionName(),
                dimensionOption.getDimensions().stream().map(DimensionDto::from).toList()
        );
    }
}
