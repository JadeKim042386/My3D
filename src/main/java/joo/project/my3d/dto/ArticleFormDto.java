package joo.project.my3d.dto;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;

import java.time.LocalDateTime;

public record ArticleFormDto(
        Long id,
        UserAccountDto userAccountDto,
        ArticleFileWithDimensionDto articleFileWithDimensionDto,
        String title,
        String content,
        ArticleType articleType,
        ArticleCategory articleCategory,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy) {
    public static ArticleFormDto of(
            Long id,
            UserAccountDto userAccountDto,
            ArticleFileWithDimensionDto articleFileDto,
            String title,
            String content,
            ArticleType articleType,
            ArticleCategory articleCategory,
            LocalDateTime createdAt,
            String createdBy,
            LocalDateTime modifiedAt,
            String modifiedBy) {
        return new ArticleFormDto(
                id,
                userAccountDto,
                articleFileDto,
                title,
                content,
                articleType,
                articleCategory,
                createdAt,
                createdBy,
                modifiedAt,
                modifiedBy);
    }

    public static ArticleFormDto from(Article article) {
        return ArticleFormDto.of(
                article.getId(),
                UserAccountDto.from(article.getUserAccount()),
                ArticleFileWithDimensionDto.from(article.getArticleFile()),
                article.getTitle(),
                article.getContent(),
                article.getArticleType(),
                article.getArticleCategory(),
                article.getCreatedAt(),
                article.getCreatedBy(),
                article.getModifiedAt(),
                article.getModifiedBy());
    }
}
