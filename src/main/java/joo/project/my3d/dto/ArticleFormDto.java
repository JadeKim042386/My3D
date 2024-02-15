package joo.project.my3d.dto;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;

public record ArticleFormDto(
        Long id,
        ArticleFileWithDimensionDto articleFileWithDimensionDto,
        String title,
        String content,
        ArticleType articleType,
        ArticleCategory articleCategory) {
    public static ArticleFormDto of(
            Long id,
            ArticleFileWithDimensionDto articleFileDto,
            String title,
            String content,
            ArticleType articleType,
            ArticleCategory articleCategory) {
        return new ArticleFormDto(id, articleFileDto, title, content, articleType, articleCategory);
    }

    public static ArticleFormDto from(Article article) {
        return ArticleFormDto.of(
                article.getId(),
                ArticleFileWithDimensionDto.from(article.getArticleFile()),
                article.getTitle(),
                article.getContent(),
                article.getArticleType(),
                article.getArticleCategory());
    }
}
