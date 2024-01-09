package joo.project.my3d.dto;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;

import java.time.LocalDateTime;

public record ArticlePreviewDto(
        Long id,
        String nickname,
        String title,
        String content,
        ArticleType articleType,
        ArticleCategory articleCategory,
        int likeCount,
        ArticleFileDto articleFileDto,
        LocalDateTime createdAt
) {

    public static ArticlePreviewDto of(Long id, String nickname, String title, String content, ArticleType articleType, ArticleCategory articleCategory, Integer likeCount, ArticleFileDto articleFileDto, LocalDateTime createdAt) {
        return new ArticlePreviewDto(id, nickname, title, content, articleType, articleCategory, likeCount, articleFileDto, createdAt);
    }

    public static ArticlePreviewDto from(Article article) {
        return ArticlePreviewDto.of(
                article.getId(),
                article.getUserAccount().getNickname(),
                article.getTitle(),
                article.getContent(),
                article.getArticleType(),
                article.getArticleCategory(),
                article.getLikeCount(),
                ArticleFileDto.from(article.getArticleFile()),
                article.getCreatedAt()
        );
    }
}
