package joo.project.my3d.dto;

import joo.project.my3d.domain.Article;
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
        Integer likeCount,
        ArticleFileDto articleFileDto,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    public static ArticleDto of(Long id, UserAccountDto userAccountDto,  String title, String content, ArticleType articleType, ArticleCategory articleCategory, Integer likeCount, ArticleFileDto articleFileDto, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleDto(id, userAccountDto, title, content, articleType, articleCategory, likeCount, articleFileDto, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleDto of(UserAccountDto userAccountDto, String title, String content, ArticleType articleType, ArticleCategory articleCategory, Integer likeCount, ArticleFileDto articleFileDto) {
        return new ArticleDto(null, userAccountDto, title, content, articleType, articleCategory, likeCount, articleFileDto, null, null, null, null);
    }

    public static ArticleDto from(Article article) {
        return ArticleDto.of(
                article.getId(),
                UserAccountDto.from(article.getUserAccount()),
                article.getTitle(),
                article.getContent(),
                article.getArticleType(),
                article.getArticleCategory(),
                article.getLikeCount(),
                ArticleFileDto.from(article.getArticleFile()),
                article.getCreatedAt(),
                article.getCreatedBy(),
                article.getModifiedAt(),
                article.getModifiedBy()
        );
    }

    public Article toEntity() {
        return Article.of(
            userAccountDto.toEntity(),
            title,
            content,
            articleType,
            articleCategory,
            articleFileDto.toEntity()
        );
    }
}
