package joo.project.my3d.dto.response;

import joo.project.my3d.dto.ArticleDto;

public record ArticleFormResponse(
        Long id,
        ArticleFileResponse file,
        String title,
        String content,
        String articleCategory
) {
    public static ArticleFormResponse of(Long id, ArticleFileResponse file, String title, String content, String articleCategory) {
        return new ArticleFormResponse(id, file, title, content, articleCategory);
    }

    public static ArticleFormResponse from(ArticleDto dto) {
        return ArticleFormResponse.of(
                dto.id(),
                ArticleFileResponse.from(dto.articleFileDto()),
                dto.title(),
                dto.content(),
                dto.articleCategory().name()
        );
    }
}
