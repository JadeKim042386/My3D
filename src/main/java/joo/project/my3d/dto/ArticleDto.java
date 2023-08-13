package joo.project.my3d.dto;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;

import java.time.LocalDateTime;

public record ArticleDto(
        Long id,
        UserAccountDto userAccountDto,
        ArticleFileDto articleFileDto,
        String title,
        String content,
        ArticleType articleType,
        ArticleCategory articleCategory,
        int likeCount,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    public static ArticleDto of(Long id, UserAccountDto userAccountDto, ArticleFileDto articleFileDto, String title, String content, ArticleType articleType, ArticleCategory articleCategory, int likeCount, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleDto(id, userAccountDto, articleFileDto, title, content, articleType, articleCategory, likeCount, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleDto of(UserAccountDto userAccountDto, ArticleFileDto articleFileDto, String title, String content, ArticleType articleType, ArticleCategory articleCategory, int likeCount) {
        return new ArticleDto(null, userAccountDto, articleFileDto, title, content, articleType, articleCategory, likeCount, null, null, null, null);
    }

    public static ArticleDto from(Article article) {
        return ArticleDto.of(
                article.getId(),
                UserAccountDto.from(article.getUserAccount()),
                ArticleFileDto.from(article.getArticleFile()),
                article.getTitle(),
                article.getContent(),
                article.getArticleType(),
                article.getArticleCategory(),
                article.getLikeCount(),
                article.getCreatedAt(),
                article.getCreatedBy(),
                article.getModifiedAt(),
                article.getModifiedBy()
        );
    }

    public Article toEntity() {
        return Article.of(
            userAccountDto.toEntity(),
            articleFileDto.toEntity(),
            title,
            content,
            articleType,
            articleCategory
        );
    }
}
