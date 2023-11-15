package joo.project.my3d.dto;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.DimensionOption;

public record DimensionOptionDto(
        Long id,
        Long articleId,
        String optionName
) {
    public static DimensionOptionDto of(Long id, Long articleId, String optionName) {
        return new DimensionOptionDto(id, articleId, optionName);
    }

    public static DimensionOptionDto of(Long articleId, String optionName) {
        return DimensionOptionDto.of(null, articleId, optionName);
    }

    public DimensionOption toEntity(Article article) {
        return DimensionOption.of(
                article,
                optionName
        );
    }

    public static DimensionOptionDto from(DimensionOption dimensionOption) {
        return DimensionOptionDto.of(
                dimensionOption.getId(),
                dimensionOption.getOptionName()
        );
    }
}
