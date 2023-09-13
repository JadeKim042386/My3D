package joo.project.my3d.dto;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.GoodOption;

import java.util.List;

public record GoodOptionWithDimensionDto(
        Long articleId,
        String optionName,
        Integer addPrice,
        String printingTech,
        String material,
        List<DimensionDto> dimensionDtos
) {
    public static GoodOptionWithDimensionDto of(Long articleId, String optionName, Integer addPrice, String printingTech, String material, List<DimensionDto> dimensionDtos) {
        return new GoodOptionWithDimensionDto(articleId, optionName, addPrice, printingTech, material, dimensionDtos);
    }

    public GoodOption toEntity(Article article) {
        return GoodOption.of(
                article,
                optionName,
                addPrice,
                printingTech,
                material
        );
    }

    public static GoodOptionWithDimensionDto from(GoodOption goodOption) {
        return GoodOptionWithDimensionDto.of(
                goodOption.getArticle().getId(),
                goodOption.getOptionName(),
                goodOption.getAddPrice(),
                goodOption.getPrintingTech(),
                goodOption.getMaterial(),
                goodOption.getDimensions().stream().map(DimensionDto::from).toList()
        );
    }
}
