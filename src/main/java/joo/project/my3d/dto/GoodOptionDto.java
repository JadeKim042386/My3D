package joo.project.my3d.dto;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.GoodOption;

public record GoodOptionDto(
        Long id,
        Long articleId,
        String optionName,
        Integer addPrice,
        String printingTech,
        String material
) {
    public static GoodOptionDto of(Long id, Long articleId, String optionName, Integer addPrice, String printingTech, String material) {
        return new GoodOptionDto(id, articleId, optionName, addPrice, printingTech, material);
    }

    public static GoodOptionDto of(Long articleId, String optionName, Integer addPrice, String printingTech, String material) {
        return GoodOptionDto.of(null, articleId, optionName, addPrice, printingTech, material);
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

    public static GoodOptionDto from(GoodOption goodOption) {
        return GoodOptionDto.of(
                goodOption.getId(),
                goodOption.getArticle().getId(),
                goodOption.getOptionName(),
                goodOption.getAddPrice(),
                goodOption.getPrintingTech(),
                goodOption.getMaterial()
        );
    }
}
