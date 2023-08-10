package joo.project.my3d.dto.response;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.dto.ArticleCommentDto;
import joo.project.my3d.dto.ArticleFileDto;
import joo.project.my3d.dto.ArticleWithCommentsAndLikeCountDto;
import joo.project.my3d.dto.UserAccountDto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record ArticleWithCommentsAndLikeCountResponse(
        Long id,
        String nickname,
        ArticleFileResponse articleFileResponse,
        String title,
        String content,
        ArticleType articleType,
        ArticleCategory articleCategory,
        Set<ArticleCommentResponse> articleCommentResponses,
        int likeCount,
        LocalDateTime createdAt
) {
    public static ArticleWithCommentsAndLikeCountResponse of(Long id, String nickname, ArticleFileResponse articleFileResponse, String title, String content, ArticleType articleType, ArticleCategory articleCategory, Set<ArticleCommentResponse> articleCommentResponses, int likeCount, LocalDateTime createdAt) {
        return new ArticleWithCommentsAndLikeCountResponse(id, nickname, articleFileResponse, title, content, articleType, articleCategory, articleCommentResponses, likeCount, createdAt);
    }

    public static ArticleWithCommentsAndLikeCountResponse from(ArticleWithCommentsAndLikeCountDto dto) {
        return ArticleWithCommentsAndLikeCountResponse.of(
                dto.id(),
                dto.userAccountDto().nickname(),
                ArticleFileResponse.from(dto.articleFileDto()),
                dto.title(),
                dto.content(),
                dto.articleType(),
                dto.articleCategory(),
                dto.articleCommentDtos().stream()
                        .map(ArticleCommentResponse::from)
                        .collect(Collectors.toUnmodifiableSet()),
                dto.likeCount(),
                dto.createdAt()
        );
    }
}
