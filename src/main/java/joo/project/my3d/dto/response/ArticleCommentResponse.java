package joo.project.my3d.dto.response;

import joo.project.my3d.dto.ArticleCommentDto;

public record ArticleCommentResponse (
        String content,
        String createdAt,
        String nickname,
        String email,
        Long parentCommentId
) {
    public static ArticleCommentResponse from(ArticleCommentDto dto) {
        return new ArticleCommentResponse(
                dto.content(),
                dto.createdAt(),
                dto.nickname(),
                dto.email(),
                dto.parentCommentId()
        );
    }
}
