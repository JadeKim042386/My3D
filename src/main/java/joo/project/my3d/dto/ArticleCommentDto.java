package joo.project.my3d.dto;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.ArticleComment;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.utils.LocalDateTimeUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public record ArticleCommentDto(
        Long id,
        Long articleId,
        String content,
        String createdAt,
        String nickname,
        String email,
        Long parentCommentId,
        Set<ArticleCommentDto> childComments
) {
    public static ArticleCommentDto of(Long id, Long articleId, String content, LocalDateTime createdAt, String nickname, String email, Long parentCommentId) {
        Comparator<ArticleCommentDto> childCommentComparator = Comparator
                .comparing(ArticleCommentDto::createdAt)
                .thenComparingLong(ArticleCommentDto::id);
        return new ArticleCommentDto(id, articleId, content, LocalDateTimeUtils.passedTime(createdAt), nickname, email, parentCommentId, new TreeSet<>(childCommentComparator));
    }

    /**
     * 댓글 추가 (ArticleCommentRequest -> ArticleCommentDto)
     */
    public static ArticleCommentDto of(Long articleId, String content, String nickname, String email, Long parentCommentId) {
        return ArticleCommentDto.of(null, articleId, content, null, nickname, email, parentCommentId);
    }

    public static ArticleCommentDto from(ArticleComment articleComment) {
        return ArticleCommentDto.of(
                articleComment.getId(),
                articleComment.getArticle().getId(),
                articleComment.getContent(),
                articleComment.getCreatedAt(),
                articleComment.getUserAccount().getNickname(),
                articleComment.getUserAccount().getEmail(),
                articleComment.getParentCommentId()
        );
    }

    public ArticleComment toEntity(Article article, UserAccount userAccount) {
        return ArticleComment.of(
                userAccount,
                article,
                content
        );
    }

    public boolean hasParentComment() {
        return parentCommentId != null;
    }
}
