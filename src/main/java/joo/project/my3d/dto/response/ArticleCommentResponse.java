package joo.project.my3d.dto.response;

import joo.project.my3d.dto.ArticleCommentDto;
import joo.project.my3d.utils.LocalDateTimeUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public record ArticleCommentResponse(
        Long id,
        String content,
        String createdAt,
        String nickname,
        String email,
        Long parentCommentId,
        Set<ArticleCommentResponse> childComments
) {
    public static ArticleCommentResponse of(Long id, String content, LocalDateTime createdAt, String nickname, String email) {
        return ArticleCommentResponse.of(id, content, createdAt, nickname, email, null);
    }

    public static ArticleCommentResponse of(Long id, String content, LocalDateTime createdAt, String nickname, String email, Long parentCommentId) {
        Comparator<ArticleCommentResponse> childCommentComparator = Comparator
                .comparing(ArticleCommentResponse::createdAt)
                .thenComparingLong(ArticleCommentResponse::id);
        return new ArticleCommentResponse(id, content, LocalDateTimeUtils.passedTime(createdAt), nickname, email, parentCommentId, new TreeSet<>(childCommentComparator));
    }

    public static ArticleCommentResponse from(ArticleCommentDto dto) {
        return ArticleCommentResponse.of(
                dto.id(),
                dto.content(),
                dto.createdAt(),
                dto.userAccountDto().nickname(),
                dto.userAccountDto().email(),
                dto.parentCommentId()
        );
    }

    public boolean hasParentComment() {
        return parentCommentId != null;
    }
}
