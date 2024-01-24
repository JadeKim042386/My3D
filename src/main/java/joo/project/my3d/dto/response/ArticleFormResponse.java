package joo.project.my3d.dto.response;

import joo.project.my3d.domain.constant.FormStatus;
import joo.project.my3d.dto.ArticleFileWithDimensionDto;
import joo.project.my3d.dto.ArticleFormDto;

public record ArticleFormResponse(
        Long id,
        FormStatus formStatus,
        ArticleFileWithDimensionDto modelFile,
        String title,
        String content,
        String articleCategory
) {
    public static ArticleFormResponse of(FormStatus formStatus) {
        return ArticleFormResponse.of(null, formStatus, null,  null, null, null);
    }

    public static ArticleFormResponse of(Long id, FormStatus formStatus, ArticleFileWithDimensionDto modelFile, String title, String content, String articleCategory) {
        return new ArticleFormResponse(id, formStatus, modelFile, title, content, articleCategory);
    }

    /**
     * 게시글 수정 폼
     */
    public static ArticleFormResponse from(ArticleFormDto dto, FormStatus formStatus) {

        return ArticleFormResponse.of(
                dto.id(),
                formStatus,
                dto.articleFileWithDimensionDto(),
                dto.title(),
                dto.content(),
                dto.articleCategory().name()
        );
    }
}
