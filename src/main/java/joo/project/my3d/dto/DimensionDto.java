package joo.project.my3d.dto;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.Dimension;
import joo.project.my3d.domain.constant.DimUnit;

public record DimensionDto(
        Long id,
        Long articleId,
        String dimName,
        Float dimValue,
        DimUnit dimUnit
) {

    public static DimensionDto of(Long id, Long articleId, String dimName, Float dimValue, DimUnit dimUnit) {
        return new DimensionDto(id, articleId, dimName, dimValue, dimUnit);
    }

    public Dimension toEntity(Article article) {
        return Dimension.of(
                article,
                dimName,
                dimValue,
                dimUnit
        );
    }

    public static DimensionDto from(Dimension dimension) {
        return DimensionDto.of(
                dimension.getId(),
                dimension.getArticle().getId(),
                dimension.getDimName(),
                dimension.getDimValue(),
                dimension.getDimUnit()
        );
    }
}
