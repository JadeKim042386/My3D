package joo.project.my3d.dto;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;

import java.time.LocalDateTime;
import java.util.List;

public record ArticleDto(
        Long id,
        UserAccountDto userAccountDto,
        List<ArticleFileDto> articleFileDtos,
        String title,
        String summary,
        String content,
        ArticleType articleType,
        ArticleCategory articleCategory,
        Integer likeCount,
        PriceDto priceDto,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    public static ArticleDto of(Long id, UserAccountDto userAccountDto, List<ArticleFileDto> articleFileDtos, String title, String summary, String content, ArticleType articleType, ArticleCategory articleCategory, Integer likeCount, PriceDto priceDto, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleDto(id, userAccountDto, articleFileDtos, title, summary, content, articleType, articleCategory, likeCount, priceDto, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleDto of(UserAccountDto userAccountDto, List<ArticleFileDto> articleFileDtos, String title, String summary, String content, ArticleType articleType, ArticleCategory articleCategory, Integer likeCount, PriceDto priceDto) {
        return new ArticleDto(null, userAccountDto, articleFileDtos, title, summary, content, articleType, articleCategory, likeCount, priceDto, null, null, null, null);
    }

    public static ArticleDto from(Article article) {
        return ArticleDto.of(
                article.getId(),
                UserAccountDto.from(article.getUserAccount()),
                article.getArticleFiles().stream()
                        .map(ArticleFileDto::from)
                        .toList(),
                article.getTitle(),
                article.getSummary(),
                article.getContent(),
                article.getArticleType(),
                article.getArticleCategory(),
                article.getLikeCount(),
                PriceDto.from(article.getPrice()),
                article.getCreatedAt(),
                article.getCreatedBy(),
                article.getModifiedAt(),
                article.getModifiedBy()
        );
    }

    public Article toEntity() {
        return Article.of(
            userAccountDto.toEntity(),
            title,
            summary,
            content,
            articleType,
            articleCategory,
            priceDto.toEntity()
        );
    }
}
