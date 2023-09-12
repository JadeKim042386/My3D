package joo.project.my3d.dto;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.Price;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public record ArticleWithCommentsAndLikeCountDto(
        Long id,
        UserAccountDto userAccountDto,
        Set<ArticleFileDto> articleFileDtos,
        String title,
        String summary,
        String content,
        ArticleType articleType,
        ArticleCategory articleCategory,
        Set<ArticleCommentDto> articleCommentDtos,
        int likeCount,
        PriceDto priceDto,
        List<GoodOptionDto> goodOptionDtos,
        List<DimensionDto> dimensionDtos,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static ArticleWithCommentsAndLikeCountDto of(Long id, UserAccountDto userAccountDto, Set<ArticleFileDto> articleFileDtos, String title, String summary, String content, ArticleType articleType, ArticleCategory articleCategory, Set<ArticleCommentDto> articleCommentDtos, int likeCount, PriceDto priceDto, List<GoodOptionDto> goodOptionDtos, List<DimensionDto> dimensionDtos, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleWithCommentsAndLikeCountDto(id, userAccountDto, articleFileDtos, title, summary, content, articleType, articleCategory, articleCommentDtos, likeCount, priceDto, goodOptionDtos, dimensionDtos, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleWithCommentsAndLikeCountDto from(Article article) {
        return ArticleWithCommentsAndLikeCountDto.of(
                article.getId(),
                UserAccountDto.from(article.getUserAccount()),
                article.getArticleFiles().stream()
                        .map(ArticleFileDto::from)
                        .collect(Collectors.toUnmodifiableSet()),
                article.getTitle(),
                article.getSummary(),
                article.getContent(),
                article.getArticleType(),
                article.getArticleCategory(),
                article.getArticleComments().stream()
                        .map(ArticleCommentDto::from)
                        .collect(Collectors.toUnmodifiableSet()),
                article.getLikeCount(),
                PriceDto.from(article.getPrice()),
                article.getGoodOptions().stream().map(GoodOptionDto::from).toList(),
                article.getDimensions().stream().map(DimensionDto::from).toList(),
                article.getCreatedAt(),
                article.getCreatedBy(),
                article.getModifiedAt(),
                article.getModifiedBy()
        );
    }
}
