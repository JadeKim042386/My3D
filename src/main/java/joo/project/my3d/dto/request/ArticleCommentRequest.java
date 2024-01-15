package joo.project.my3d.dto.request;

import joo.project.my3d.dto.ArticleCommentDto;

public record ArticleCommentRequest(
        Long articleId,
        String content,
        Long parentCommentId
) {
    public static ArticleCommentRequest of(Long articleId, String content, Long parentCommentId) {
        return new ArticleCommentRequest(articleId, content, parentCommentId);
    }

    public static ArticleCommentRequest of(Long articleId, String content) {
        return ArticleCommentRequest.of(articleId, content, null);
    }

    public ArticleCommentDto toDto(String nickname, String email) {
        return ArticleCommentDto.of(
                articleId,
                content,
                nickname,
                email,
                parentCommentId
        );
    }
}
