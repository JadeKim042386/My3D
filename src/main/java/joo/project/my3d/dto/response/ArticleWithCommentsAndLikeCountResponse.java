package joo.project.my3d.dto.response;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.dto.ArticleCommentDto;
import joo.project.my3d.dto.ArticleFileDto;
import joo.project.my3d.dto.ArticleWithCommentsAndLikeCountDto;
import joo.project.my3d.dto.UserAccountDto;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public record ArticleWithCommentsAndLikeCountResponse(
        Long id,
        String userId,
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
    public static ArticleWithCommentsAndLikeCountResponse of(Long id, String userId, String nickname, ArticleFileResponse articleFileResponse, String title, String content, ArticleType articleType, ArticleCategory articleCategory, Set<ArticleCommentResponse> articleCommentResponses, int likeCount, LocalDateTime createdAt) {
        return new ArticleWithCommentsAndLikeCountResponse(id, userId, nickname, articleFileResponse, title, content, articleType, articleCategory, articleCommentResponses, likeCount, createdAt);
    }

    public static ArticleWithCommentsAndLikeCountResponse from(ArticleWithCommentsAndLikeCountDto dto) {
        return ArticleWithCommentsAndLikeCountResponse.of(
                dto.id(),
                dto.userAccountDto().userId(),
                dto.userAccountDto().nickname(),
                ArticleFileResponse.from(dto.articleFileDto()),
                dto.title(),
                dto.content(),
                dto.articleType(),
                dto.articleCategory(),
                organizeChildComments(dto.articleCommentDtos()),
                dto.likeCount(),
                dto.createdAt()
        );
    }

    private static Set<ArticleCommentResponse> organizeChildComments(Set<ArticleCommentDto> dtos) {
        Map<Long, ArticleCommentResponse> map = dtos.stream()
                .map(ArticleCommentResponse::from)
                .collect(Collectors.toMap(ArticleCommentResponse::id, Function.identity()));
        //부모 댓글을 가지는 자식 댓글을 filtering하여 부모 댓글 안에 삽입
        map.values().stream()
                .filter(ArticleCommentResponse::hasParentComment)
                .forEach(comment -> {
                    ArticleCommentResponse parentComment = map.get(comment.parentCommentId());
                    parentComment.childComments().add(comment);
                });

        //부모 댓글만 filtering하고 정렬
        return map.values().stream()
                .filter(comment -> !comment.hasParentComment())
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator
                                .comparing(ArticleCommentResponse::createdAt)
                                .reversed()
                                .thenComparingLong(ArticleCommentResponse::id)
                        )
                ));
    }
}
