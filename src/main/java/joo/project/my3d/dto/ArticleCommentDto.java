package joo.project.my3d.dto;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.ArticleComment;
import joo.project.my3d.domain.UserAccount;

import java.time.LocalDateTime;

public record ArticleCommentDto(
        Long id,
        String content,
        Long articleId,
        Long parentCommentId,
        UserAccountDto userAccountDto,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static ArticleCommentDto of(Long id, String content, Long articleId, Long parentCommentId, UserAccountDto userAccountDto, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleCommentDto(id, content, articleId, parentCommentId, userAccountDto, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleCommentDto of(Long articleId, Long parentCommentId, UserAccountDto userAccountDto, String content) {
        return new ArticleCommentDto(null, content, articleId, parentCommentId, userAccountDto, null, null, null, null);
    }

    public static ArticleCommentDto of(Long articleId, UserAccountDto userAccountDto, String content) {
        return ArticleCommentDto.of(articleId, null, userAccountDto, content);
    }

    public static ArticleCommentDto from(ArticleComment articleComment) {
        return ArticleCommentDto.of(
                articleComment.getId(),
                articleComment.getContent(),
                articleComment.getArticle().getId(),
                articleComment.getParentCommentId(),
                UserAccountDto.from(articleComment.getUserAccount()),
                articleComment.getCreatedAt(),
                articleComment.getCreatedBy(),
                articleComment.getModifiedAt(),
                articleComment.getModifiedBy()
        );
    }

    public ArticleComment toEntity(Article article, UserAccount userAccount) {
        return ArticleComment.of(
                userAccount,
                article,
                content
        );
    }
}
