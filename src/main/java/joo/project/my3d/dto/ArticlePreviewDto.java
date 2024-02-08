package joo.project.my3d.dto;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.utils.LocalDateTimeUtils;

import java.time.LocalDateTime;

public record ArticlePreviewDto(
        Long id,
        Integer likeCount,
        String nickname,
        String title,
        String content,
        ArticleType articleType,
        ArticleCategory articleCategory,
        ArticleFileDto articleFile,
        String createdAt
) {
    public static ArticlePreviewDto of(Long id, Integer likeCount, String nickname, String title, String content, ArticleType articleType, ArticleCategory articleCategory, ArticleFileDto articleFileDto, LocalDateTime createdAt) {
        return new ArticlePreviewDto(id, likeCount, nickname, title, content, articleType, articleCategory, articleFileDto, LocalDateTimeUtils.passedTime(createdAt));
    }

    public static ArticlePreviewDto from(Article article) {
        return ArticlePreviewDto.of(
                article.getId(),
                article.getArticleLikes().size(),
                article.getUserAccount().getNickname(),
                article.getTitle(),
                article.getContent(),
                article.getArticleType(),
                article.getArticleCategory(),
                ArticleFileDto.from(article.getArticleFile()),
                article.getCreatedAt()
        );
    }
}
