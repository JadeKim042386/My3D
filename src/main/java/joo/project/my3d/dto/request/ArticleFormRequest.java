package joo.project.my3d.dto.request;

import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.dto.*;
import joo.project.my3d.dto.validation.InCategory;
import joo.project.my3d.dto.validation.MultipartFileSizeValid;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class ArticleFormRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String summary;
    @NotBlank
    private String content;
    @NotNull
    private Integer priceValue;
    @NotNull
    private Integer deliveryPrice;
    @NotNull
    @Size(min=1, message = "최소 1개 이상의 상품 옵션을 추가해주세요")
    private final List<GoodOptionRequest> goodOptionRequests = new ArrayList<>();
    @NotNull
    private final List<DimensionRequest> dimensionRequests = new ArrayList<>();
    @MultipartFileSizeValid
    private MultipartFile modelFile;
    private MultipartFile[] imgFiles;
    @InCategory
    private String articleCategory;

    public List<MultipartFile> getFiles() {
        List<MultipartFile> files = new ArrayList<>();
        if (imgFiles != null){
            files.addAll(Arrays.stream(imgFiles).toList());
        }
        files.add(modelFile);
        return files;
    }

    public ArticleDto toArticleDto(UserAccountDto userAccountDto, ArticleType articleType) {
        return ArticleDto.of(
                userAccountDto,
                title,
                summary,
                content,
                articleType,
                ArticleCategory.valueOf(articleCategory),
                null,
                PriceDto.of(priceValue, deliveryPrice)
        );
    }

    public ArticleDto toArticleDto(UserAccountDto userAccountDto) {
        return toArticleDto(userAccountDto, null);
    }

    public List<GoodOptionDto> toGoodOptionDtos(Long articleId) {
        return goodOptionRequests.stream()
                .map(goodOptionRequest -> goodOptionRequest.toDto(articleId))
                .toList();
    }

    public List<DimensionDto> toDimensionDtos(Long articleId) {
        return dimensionRequests.stream()
                .map(dimensionRequest -> dimensionRequest.toDto(articleId))
                .toList();
    }

}
