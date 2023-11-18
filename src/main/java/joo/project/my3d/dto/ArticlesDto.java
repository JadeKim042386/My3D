package joo.project.my3d.dto;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;

import java.time.LocalDateTime;

public record ArticlesDto(
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

    public static ArticlesDto of(Long id, UserAccountDto userAccountDto, String title, String content, ArticleType articleType, ArticleCategory articleCategory, Integer likeCount, ArticleFileDto articleFileDto, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticlesDto(id, userAccountDto, title, content, articleType, articleCategory, likeCount, articleFileDto, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticlesDto of(UserAccountDto userAccountDto, String title, String content, ArticleType articleType, ArticleCategory articleCategory, Integer likeCount, ArticleFileDto articleFileDto) {
        return new ArticlesDto(null, userAccountDto, title, content, articleType, articleCategory, likeCount, articleFileDto, null, null, null, null);
    }

    public static ArticlesDto from(Article article) {
        return ArticlesDto.of(
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
}
