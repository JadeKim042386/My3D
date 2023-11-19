package joo.project.my3d.dto.response;

import joo.project.my3d.dto.ArticleFileWithDimensionOptionWithDimensionDto;
import joo.project.my3d.dto.ArticleFormDto;
import joo.project.my3d.dto.ArticleWithCommentsAndLikeCountDto;
import joo.project.my3d.dto.request.ArticleFormRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ArticleFormResponse {
    private Long id;
    private ArticleFileWithDimensionResponse modelFile;
    private String title;
    private String content;
    private String articleCategory;

    private ArticleFormResponse(Long id, ArticleFileWithDimensionResponse modelFile, String title, String content, String articleCategory) {
        this.id = id;
        this.modelFile = modelFile;
        this.title = title;
        this.content = content;
        this.articleCategory = articleCategory;
    }

    public static ArticleFormResponse of() {
        return new ArticleFormResponse(null, null, null, null,  null);
    }

    public static ArticleFormResponse of(Long id, ArticleFileWithDimensionResponse modelFile, String title, String content, String articleCategory) {
        return new ArticleFormResponse(id, modelFile, title, content, articleCategory);
    }

    public static ArticleFormResponse from(ArticleFormDto dto) {

        return ArticleFormResponse.of(
                dto.id(),
                ArticleFileWithDimensionResponse.from(dto.articleFileWithDimensionOptionWithDimensionDto()),
                dto.title(),
                dto.content(),
                dto.articleCategory().name()
        );
    }

    public static ArticleFormResponse from(Long articleId, ArticleFormRequest request) {

        return ArticleFormResponse.of(
                articleId,
                ArticleFileWithDimensionResponse.from(request.getModelFile(), request.getDimensionOptions()),
                request.getTitle(),
                request.getContent(),
                request.getArticleCategory()
        );
    }
}
