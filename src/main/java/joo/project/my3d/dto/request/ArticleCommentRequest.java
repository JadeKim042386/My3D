package joo.project.my3d.dto.request;

import joo.project.my3d.dto.ArticleCommentDto;
import joo.project.my3d.dto.UserAccountDto;

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

    public ArticleCommentDto toDto(UserAccountDto userAccountDto) {
        return ArticleCommentDto.of(
                articleId,
                parentCommentId,
                userAccountDto,
                content
        );
    }
}
