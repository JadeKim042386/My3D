package joo.project.my3d.dto.request;

import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.dto.*;
import joo.project.my3d.dto.validation.InCategory;
import joo.project.my3d.dto.validation.MultipartFileSizeValid;
import joo.project.my3d.utils.FileUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ArticleFormRequest {
    private Long id;
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
    private final List<GoodOptionRequest> goodOptions = new ArrayList<>();
    @MultipartFileSizeValid
    private MultipartFile modelFile;
    @InCategory
    private String articleCategory;

    /**
     * 게시글 등록시 사용
     * @param articleFileDto 저장한 파일의 DTO
     * @param userAccountDto 작성자의 DTO
     * @param articleType
     */
    public ArticleDto toArticleDto(ArticleFileDto articleFileDto, UserAccountDto userAccountDto, ArticleType articleType) {
        return ArticleDto.of(
                userAccountDto,
                title,
                summary,
                content,
                articleType,
                ArticleCategory.valueOf(articleCategory),
                0,
                articleFileDto,
                PriceDto.of(priceValue, deliveryPrice)
        );
    }

    /**
     * 게시글 업데이트시 사용
     */
    public ArticleDto toArticleDto(UserAccountDto userAccountDto) {
        return toArticleDto(null, userAccountDto, null);
    }

    public List<GoodOptionDto> toGoodOptionDtos(Long articleId) {
        return goodOptions.stream()
                .map(goodOptionRequest -> goodOptionRequest.toDto(articleId))
                .toList();
    }
}
