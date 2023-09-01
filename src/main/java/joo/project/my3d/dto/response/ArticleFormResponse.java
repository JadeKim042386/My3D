package joo.project.my3d.dto.response;

import joo.project.my3d.dto.ArticleDto;

import java.util.List;

public record ArticleFormResponse(
        Long id,
        List<ArticleFileResponse> files,
        String title,
        String content,
        String articleCategory
) {
    public static ArticleFormResponse of(Long id, List<ArticleFileResponse> files, String title, String content, String articleCategory) {
        return new ArticleFormResponse(id, files, title, content, articleCategory);
    }

    public static ArticleFormResponse from(ArticleDto dto) {
        return ArticleFormResponse.of(
                dto.id(),
                dto.articleFileDtos().stream()
                        .map(ArticleFileResponse::from)
                        .toList(),
                dto.title(),
                dto.content(),
                dto.articleCategory().name()
        );
    }
}
