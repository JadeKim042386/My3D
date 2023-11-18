package joo.project.my3d.dto;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record ArticleWithCommentsAndLikeCountDto(
        Long id,
        UserAccountDto userAccountDto,
        ArticleFileWithDimensionOptionWithDimensionDto articleFileWithDimensionOptionWithDimensionDto,
        String title,
        String content,
        ArticleType articleType,
        ArticleCategory articleCategory,
        Set<ArticleCommentDto> articleCommentDtos,
        int likeCount,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static ArticleWithCommentsAndLikeCountDto of(Long id, UserAccountDto userAccountDto, ArticleFileWithDimensionOptionWithDimensionDto articleFileDto, String title, String content, ArticleType articleType, ArticleCategory articleCategory, Set<ArticleCommentDto> articleCommentDtos, int likeCount, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleWithCommentsAndLikeCountDto(id, userAccountDto, articleFileDto, title, content, articleType, articleCategory, articleCommentDtos, likeCount, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleWithCommentsAndLikeCountDto from(Article article) {
        return ArticleWithCommentsAndLikeCountDto.of(
                article.getId(),
                UserAccountDto.from(article.getUserAccount()),
                ArticleFileWithDimensionOptionWithDimensionDto.from(article.getArticleFile()),
                article.getTitle(),
                article.getContent(),
                article.getArticleType(),
                article.getArticleCategory(),
                article.getArticleComments().stream()
                        .map(ArticleCommentDto::from)
                        .collect(Collectors.toUnmodifiableSet()),
                article.getLikeCount(),
                article.getCreatedAt(),
                article.getCreatedBy(),
                article.getModifiedAt(),
                article.getModifiedBy()
        );
    }
}
