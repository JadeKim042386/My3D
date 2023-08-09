package joo.project.my3d.dto;

import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;

import java.time.LocalDateTime;
import java.util.Set;

public record ArticleWithCommentsAndLikeCountDto(
        Long id,
        UserAccountDto userAccountDto,
        ArticleFileDto articleFileDto,
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
    public static ArticleWithCommentsAndLikeCountDto of(Long id, UserAccountDto userAccountDto, ArticleFileDto articleFileDto, String title, String content, ArticleType articleType, ArticleCategory articleCategory, Set<ArticleCommentDto> articleCommentDtos, int likeCount, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleWithCommentsAndLikeCountDto(id, userAccountDto, articleFileDto, title, content, articleType, articleCategory, articleCommentDtos, likeCount, createdAt, createdBy, modifiedAt, modifiedBy);
    }
}
