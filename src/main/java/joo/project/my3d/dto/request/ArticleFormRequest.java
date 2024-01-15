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
import javax.validation.constraints.Null;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ArticleFormRequest {
    @Null
    private Long id;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotNull
    @Size(min=1, max=1, message = "상품 옵션 1개만 추가해주세요")
    private final List<DimensionOptionRequest> dimensionOptions = new ArrayList<>();
    @MultipartFileSizeValid
    private MultipartFile modelFile;
    @InCategory
    private String articleCategory;

    /**
     * 게시글 등록시 사용
     * @param userAccountDto 작성자의 DTO
     * @param articleType
     */
    public ArticleDto toArticleDto(ArticleFileWithDimensionDto articleFile, UserAccountDto userAccountDto, ArticleType articleType) {
        return ArticleDto.of(
                userAccountDto,
                title,
                content,
                articleType,
                ArticleCategory.valueOf(articleCategory),
                articleFile
        );
    }

    /**
     * 게시글 업데이트시 사용
     */
    public ArticleDto toArticleDto() {
        return toArticleDto(null, null, ArticleType.MODEL);
    }

    /**
     * 하나의 DimensionOption을 반환하며 이후 기능 개선시 삭제될 예정
     */
    public DimensionOptionDto toDimensionOptionDto() {

        return dimensionOptions.get(0).toDto();
    }

    public List<DimensionDto> toDimensions(Long dimensionOptionId) {

        return dimensionOptions.get(0).toDimensionDtos(dimensionOptionId);
    }

    public ArticleFileWithDimensionDto toArticleFileWithDimensionDto() {
        String originalFileName = modelFile.getOriginalFilename();
        String extension = FileUtils.getExtension(originalFileName);

        return ArticleFileWithDimensionDto.of(
                modelFile.getSize(),
                originalFileName,
                UUID.randomUUID() + "." + extension,
                extension,
                dimensionOptions.get(0).toWithDimensionDto()
        );
    }
}
