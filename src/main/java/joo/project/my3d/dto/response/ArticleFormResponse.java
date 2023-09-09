package joo.project.my3d.dto.response;

import joo.project.my3d.dto.ArticleDto;
import joo.project.my3d.dto.ArticleFileDto;
import joo.project.my3d.dto.request.DimensionRequest;
import joo.project.my3d.dto.request.GoodOptionRequest;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
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
    private final List<GoodOptionRequest> goodOptionRequests = new ArrayList<>();
    private final List<DimensionRequest> dimensionRequests = new ArrayList<>();
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

    public static ArticleFormResponse of(Long id, ArticleFileResponse modelFile, List<ArticleFileResponse> imgFiles, String title, String summary, String content, Integer priceValue, Integer deliveryPrice, String articleCategory) {
        return new ArticleFormResponse(id, modelFile, imgFiles, title, summary, content, priceValue, deliveryPrice, articleCategory);
    }

    public static ArticleFormResponse from(ArticleDto dto, List<ArticleFileDto> articleFileDtos, List<GoodOptionRequest> goodOptionRequests, List<DimensionRequest> dimensionRequests) {
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
        articleFormResponse.goodOptionRequests.addAll(goodOptionRequests);
        articleFormResponse.dimensionRequests.addAll(dimensionRequests);
        return articleFormResponse;
    }
}
