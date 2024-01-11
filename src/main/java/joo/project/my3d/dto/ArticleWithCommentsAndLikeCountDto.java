package joo.project.my3d.dto;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.ArticleComment;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.utils.LocalDateTimeUtils;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public record ArticleWithCommentsAndLikeCountDto(
        Long id,
        String email,
        String nickname,
        ArticleFileWithDimensionDto articleFile,
        String title,
        String content,
        ArticleType articleType,
        ArticleCategory articleCategory,
        Set<ArticleCommentDto> articleComments,
        int likeCount,
        String createdAt
) {
    public static ArticleWithCommentsAndLikeCountDto of(Long id, String email, String nickname, ArticleFileWithDimensionDto articleFileWithDimensionDto, String title, String content, ArticleType articleType, ArticleCategory articleCategory, Set<ArticleCommentDto> articleCommentDtos, int likeCount, LocalDateTime createdAt) {
        return new ArticleWithCommentsAndLikeCountDto(id, email, nickname, articleFileWithDimensionDto, title, content, articleType, articleCategory, articleCommentDtos, likeCount, LocalDateTimeUtils.format(createdAt));
    }

    public static ArticleWithCommentsAndLikeCountDto from(Article article) {
        return ArticleWithCommentsAndLikeCountDto.of(
                article.getId(),
                article.getUserAccount().getEmail(),
                article.getUserAccount().getNickname(),
                ArticleFileWithDimensionDto.from(article.getArticleFile()),
                article.getTitle(),
                article.getContent(),
                article.getArticleType(),
                article.getArticleCategory(),
                organizeChildComments(article.getArticleComments()),
                article.getLikeCount(),
                article.getCreatedAt()
        );
    }

    private static Set<ArticleCommentDto> organizeChildComments(Set<ArticleComment> comments) {
        Map<Long, ArticleCommentDto> map = comments.stream()
                .map(ArticleCommentDto::from)
                .collect(Collectors.toMap(ArticleCommentDto::id, Function.identity()));
        //부모 댓글을 가지는 자식 댓글을 filtering하여 부모 댓글 안에 삽입
        map.values().stream()
                .filter(ArticleCommentDto::hasParentComment)
                .forEach(comment -> {
                    ArticleCommentDto parentComment = map.get(comment.parentCommentId());
                    parentComment.childComments().add(comment);
                });

        //부모 댓글만 filtering하고 정렬
        return map.values().stream()
                .filter(comment -> !comment.hasParentComment())
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator
                                .comparing(ArticleCommentDto::createdAt)
                                .reversed()
                                .thenComparingLong(ArticleCommentDto::id)
                        )
                ));
    }
}
