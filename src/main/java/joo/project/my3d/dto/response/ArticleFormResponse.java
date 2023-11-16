package joo.project.my3d.dto.response;

import joo.project.my3d.dto.ArticleDto;
import joo.project.my3d.dto.ArticleFileDto;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ArticleFormResponse {
    private Long id;
    private ArticleFileResponse modelFile;
    private String title;
    private String content;
    private final List<DimensionOptionResponse> dimensionOptions = new ArrayList<>();
    private String articleCategory;

    private ArticleFormResponse(Long id, ArticleFileResponse modelFile, String title, String content, String articleCategory) {
        this.id = id;
        this.modelFile = modelFile;
        this.title = title;
        this.content = content;
        this.articleCategory = articleCategory;
    }

    public static ArticleFormResponse of() {
        return new ArticleFormResponse(null, null, null, null,  null);
    }

    public static ArticleFormResponse of(Long id, ArticleFileResponse modelFile, String title, String content, String articleCategory) {
        return new ArticleFormResponse(id, modelFile, title, content, articleCategory);
    }

    public static ArticleFormResponse from(ArticleDto dto, ArticleFileDto articleFileDto) {

        return ArticleFormResponse.of(
                dto.id(),
                ArticleFileResponse.from(articleFileDto),
                dto.title(),
                dto.content(),
                dto.articleCategory().name()
        );
    }
}
