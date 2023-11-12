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
    private List<ArticleFileResponse> imgFiles;
    private String title;
    private String summary;
    private String content;
    private Integer priceValue;
    private Integer deliveryPrice;
    private final List<GoodOptionResponse> goodOptions = new ArrayList<>();
    private String articleCategory;

    private ArticleFormResponse(Long id, ArticleFileResponse modelFile, List<ArticleFileResponse> imgFiles, String title, String summary, String content, Integer priceValue, Integer deliveryPrice, String articleCategory) {
        this.id = id;
        this.modelFile = modelFile;
        this.imgFiles = imgFiles;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.priceValue = priceValue;
        this.deliveryPrice = deliveryPrice;
        this.articleCategory = articleCategory;
    }

    public static ArticleFormResponse of() {
        return new ArticleFormResponse(null, null, List.of(), null, null, null, null, null, null);
    }

    public static ArticleFormResponse of(Long id, ArticleFileResponse modelFile, List<ArticleFileResponse> imgFiles, String title, String summary, String content, Integer priceValue, Integer deliveryPrice, String articleCategory) {
        return new ArticleFormResponse(id, modelFile, imgFiles, title, summary, content, priceValue, deliveryPrice, articleCategory);
    }

    public static ArticleFormResponse from(ArticleDto dto, List<ArticleFileDto> articleFileDtos, List<GoodOptionResponse> goodOptionResponses) {
        ArticleFormResponse articleFormResponse = ArticleFormResponse.of(
                dto.id(),
                ArticleFileResponse.from(
                        articleFileDtos.stream().filter(ArticleFileDto::isModelFile)
                                .toList().get(0)
                ),
                articleFileDtos.stream()
                        .filter(ArticleFileDto::isImageFile)
                        .map(ArticleFileResponse::from)
                        .toList(),
                dto.title(),
                dto.summary(),
                dto.content(),
                dto.priceDto().priceValue(),
                dto.priceDto().deliveryPrice(),
                dto.articleCategory().name()
        );
        articleFormResponse.goodOptions.addAll(goodOptionResponses);
        return articleFormResponse;
    }
}
