package joo.project.my3d.dto;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record ArticleFormDto(
        Long id,
        UserAccountDto userAccountDto,
        ArticleFileWithDimensionOptionWithDimensionDto articleFileWithDimensionOptionWithDimensionDto,
        String title,
        String content,
        ArticleType articleType,
        ArticleCategory articleCategory,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static ArticleFormDto of(Long id, UserAccountDto userAccountDto, ArticleFileWithDimensionOptionWithDimensionDto articleFileDto, String title, String content, ArticleType articleType, ArticleCategory articleCategory, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleFormDto(id, userAccountDto, articleFileDto, title, content, articleType, articleCategory, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleFormDto from(Article article) {
        return ArticleFormDto.of(
                article.getId(),
                UserAccountDto.from(article.getUserAccount()),
                ArticleFileWithDimensionOptionWithDimensionDto.from(article.getArticleFile()),
                article.getTitle(),
                article.getContent(),
                article.getArticleType(),
                article.getArticleCategory(),
                article.getCreatedAt(),
                article.getCreatedBy(),
                article.getModifiedAt(),
                article.getModifiedBy()
        );
    }
}
