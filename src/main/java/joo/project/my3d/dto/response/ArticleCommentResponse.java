package joo.project.my3d.dto.response;

import joo.project.my3d.dto.ArticleCommentDto;
import joo.project.my3d.dto.UserAccountDto;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public record ArticleCommentResponse(
        Long id,
        String content,
        String nickname,
        LocalDateTime createdAt
) {
    public static ArticleCommentResponse of(Long id, String content, String nickname, LocalDateTime createdAt) {
        return new ArticleCommentResponse(id, content, nickname, createdAt);
    }

    public static ArticleCommentResponse from(ArticleCommentDto dto) {
        return ArticleCommentResponse.of(
                dto.id(),
                dto.content(),
                dto.userAccountDto().nickname(),
                dto.createdAt()
        );
    }
}
