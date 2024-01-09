package joo.project.my3d.dto.response;

import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.FormStatus;
import joo.project.my3d.dto.ArticleFileWithDimensionOptionWithDimensionDto;
import joo.project.my3d.dto.ArticleFormDto;
import joo.project.my3d.dto.request.ArticleFormRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ArticleFormResponse {
    private Long id;
    private FormStatus formStatus;
    private ArticleCategory[] categories;
    private ArticleFileWithDimensionOptionWithDimensionDto modelFile;
    private String title;
    private String content;
    private String articleCategory;
    private boolean valid;
    private List<String> validMessages;

    private ArticleFormResponse(Long id, FormStatus formStatus, ArticleFileWithDimensionOptionWithDimensionDto modelFile, String title, String content, String articleCategory, boolean valid, List<String> validMessages) {
        this.id = id;
        this.formStatus = formStatus;
        this.categories = ArticleCategory.values();
        this.modelFile = modelFile;
        this.title = title;
        this.content = content;
        this.articleCategory = articleCategory;
        this.valid = valid;
        this.validMessages = validMessages;
    }

    public static ArticleFormResponse of(FormStatus formStatus) {
        return ArticleFormResponse.of(null, formStatus, null,  null, null, null, true, null);
    }

    public static ArticleFormResponse of(Long id, FormStatus formStatus, ArticleFileWithDimensionOptionWithDimensionDto modelFile, String title, String content, String articleCategory, boolean isValid, List<String> validMessages) {
        return new ArticleFormResponse(id, formStatus, modelFile, title, content, articleCategory, isValid, validMessages);
    }

    /**
     * 게시글 수정 폼
     */
    public static ArticleFormResponse from(ArticleFormDto dto, FormStatus formStatus) {

        return ArticleFormResponse.of(
                dto.id(),
                formStatus,
                dto.articleFileWithDimensionOptionWithDimensionDto(),
                dto.title(),
                dto.content(),
                dto.articleCategory().name(),
                true,
                List.of()
        );
    }

    /**
     *  게시글 수정시 에러가 발생할 경우 입력된 데이터를 다시 보내야할 때 사용
     */
    public static ArticleFormResponse validError(Long articleId, ArticleFormRequest request, FormStatus formStatus, List<String> validMessages) {
        return ArticleFormResponse.of(
                articleId,
                formStatus,
                ArticleFileWithDimensionOptionWithDimensionDto.from(request.getModelFile(), request.getDimensionOptions()),
                request.getTitle(),
                request.getContent(),
                request.getArticleCategory(),
                false,
                validMessages
        );
    }

    /**
     * 게시글 추가시 에러가 발생할 경우 입력된 데이터를 다시 보내야할 때 사용
     */
    public static ArticleFormResponse validError(ArticleFormRequest request, FormStatus formStatus, List<String> validMessages) {
        return ArticleFormResponse.validError(null, request, formStatus, validMessages);
    }
}
