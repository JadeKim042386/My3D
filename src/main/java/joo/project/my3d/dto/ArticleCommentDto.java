package joo.project.my3d.dto;

import java.time.LocalDateTime;

public record ArticleCommentDto(
        Long id,
        String content,
        Long articleId,
        UserAccountDto userAccountDto,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static ArticleCommentDto of(Long id, String content, Long articleId, UserAccountDto userAccountDto, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleCommentDto(id, content, articleId, userAccountDto, createdAt, createdBy, modifiedAt, modifiedBy);
    }
}
