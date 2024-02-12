package joo.project.my3d.dto;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;

import java.time.LocalDateTime;

public record ArticleDto(
        Long id,
        UserAccountDto userAccountDto,
        String title,
        String content,
        ArticleType articleType,
        ArticleCategory articleCategory,
        ArticleFileWithDimensionDto articleFileDto,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy) {

    public static ArticleDto of(
            Long id,
            UserAccountDto userAccountDto,
            String title,
            String content,
            ArticleType articleType,
            ArticleCategory articleCategory,
            ArticleFileWithDimensionDto articleFileDto,
            LocalDateTime createdAt,
            String createdBy,
            LocalDateTime modifiedAt,
            String modifiedBy) {
        return new ArticleDto(
                id,
                userAccountDto,
                title,
                content,
                articleType,
                articleCategory,
                articleFileDto,
                createdAt,
                createdBy,
                modifiedAt,
                modifiedBy);
    }

    public static ArticleDto of(
            UserAccountDto userAccountDto,
            String title,
            String content,
            ArticleType articleType,
            ArticleCategory articleCategory,
            ArticleFileWithDimensionDto articleFileDto) {
        return new ArticleDto(
                null,
                userAccountDto,
                title,
                content,
                articleType,
                articleCategory,
                articleFileDto,
                null,
                null,
                null,
                null);
    }

    public static ArticleDto from(Article article) {
        return ArticleDto.of(
                article.getId(),
                UserAccountDto.from(article.getUserAccount()),
                article.getTitle(),
                article.getContent(),
                article.getArticleType(),
                article.getArticleCategory(),
                ArticleFileWithDimensionDto.from(article.getArticleFile()),
                article.getCreatedAt(),
                article.getCreatedBy(),
                article.getModifiedAt(),
                article.getModifiedBy());
    }

    public Article toEntity(UserAccount userAccount) {
        return Article.of(userAccount, articleFileDto.toEntity(), title, content, articleType, articleCategory);
    }
}
