package joo.project.my3d.dto;

import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;

import java.time.LocalDateTime;

public record ArticleDto(
        Long id,
        UserAccountDto userAccountDto,
        ArticleFileDto articleFileDto,
        String title,
        String content,
        ArticleType articleType,
        ArticleCategory articleCategory,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static ArticleDto of(Long id, UserAccountDto userAccountDto, ArticleFileDto articleFileDto, String title, String content, ArticleType articleType, ArticleCategory articleCategory, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new ArticleDto(id, userAccountDto, articleFileDto, title, content, articleType, articleCategory, createdAt, createdBy, modifiedAt, modifiedBy);
    }
}
