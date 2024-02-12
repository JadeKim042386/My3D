package joo.project.my3d.dto.request;

import joo.project.my3d.dto.ArticleCommentDto;

public record ArticleCommentRequest(String content, Long parentCommentId) {
    public static ArticleCommentRequest of(String content, Long parentCommentId) {
        return new ArticleCommentRequest(content, parentCommentId);
    }

    public static ArticleCommentRequest of(String content) {
        return ArticleCommentRequest.of(content, null);
    }

    public ArticleCommentDto toDto(Long articleId, String nickname, String email) {
        return ArticleCommentDto.of(articleId, content, nickname, email, parentCommentId);
    }
}
