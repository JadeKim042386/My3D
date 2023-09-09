package joo.project.my3d.dto.response;

import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.dto.ArticleCommentDto;
import joo.project.my3d.dto.ArticleFileDto;
import joo.project.my3d.dto.ArticleWithCommentsAndLikeCountDto;
import joo.project.my3d.utils.LocalDateTimeUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public record ArticleWithCommentsAndLikeCountResponse(
        Long id,
        String userId,
        String nickname,
        ArticleFileResponse modelFile,
        List<ArticleFileResponse> imgFiles,
        String title,
        String content,
        ArticleType articleType,
        ArticleCategory articleCategory,
        Set<ArticleCommentResponse> articleCommentResponses,
        int likeCount,
        String createdAt
) {
    public static ArticleWithCommentsAndLikeCountResponse of(Long id, String email, String nickname, ArticleFileResponse modelFile, List<ArticleFileResponse> imgFiles, String title, String content, ArticleType articleType, ArticleCategory articleCategory, Set<ArticleCommentResponse> articleCommentResponses, int likeCount, LocalDateTime createdAt) {
        return new ArticleWithCommentsAndLikeCountResponse(id, email, nickname, modelFile, imgFiles, title, content, articleType, articleCategory, articleCommentResponses, likeCount, LocalDateTimeUtils.format(createdAt));
    }

    public static ArticleWithCommentsAndLikeCountResponse from(ArticleWithCommentsAndLikeCountDto dto) {
        return ArticleWithCommentsAndLikeCountResponse.of(
                dto.id(),
                dto.userAccountDto().email(),
                dto.userAccountDto().nickname(),
                ArticleFileResponse.from(
                        dto.articleFileDtos().stream().filter(ArticleFileDto::isModelFile)
                                .toList().get(0)
                ),
                dto.articleFileDtos().stream()
                        .filter(ArticleFileDto::isImageFile)
                        .map(ArticleFileResponse::from)
                        .toList(),
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
