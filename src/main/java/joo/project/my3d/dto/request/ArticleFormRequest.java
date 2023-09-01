package joo.project.my3d.dto.request;

import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.dto.ArticleDto;
import joo.project.my3d.dto.ArticleFileDto;
import joo.project.my3d.dto.PriceDto;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.dto.validation.InCategory;
import joo.project.my3d.dto.validation.MultipartFileSizeValid;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public record ArticleFormRequest(
        @NotBlank
        String title,
        @NotBlank
        String summary,
        @NotBlank
        String content,
        @NotNull
        Integer priceValue,
        @NotNull
        Integer deliveryPrice,
        @MultipartFileSizeValid
        MultipartFile modelFile,
        List<MultipartFile> imgFiles,
        @InCategory
        String articleCategory
) {

    public List<MultipartFile> getFiles() {
        List<MultipartFile> files = new ArrayList<>();
        if (imgFiles != null){
            files.addAll(imgFiles);
        }
        files.add(modelFile);
        return files;
    }

    public ArticleDto toDto(UserAccountDto userAccountDto, ArticleType articleType, List<ArticleFileDto> articleFileDtos) {
        return ArticleDto.of(
                userAccountDto,
                articleFileDtos,
                title,
                summary,
                content,
                articleType,
                ArticleCategory.valueOf(articleCategory),
                null,
                PriceDto.of(priceValue, deliveryPrice)
        );
    }

    /**
     * 파일이 업데이트되지 않았다면 FileDto는 추가하지 않음
     */
    public ArticleDto toDto(UserAccountDto userAccountDto, List<ArticleFileDto> articleFileDtos, boolean isUpdated) {
        return ArticleDto.of(
                userAccountDto,
                isUpdated ? articleFileDtos : null,
                title,
                summary,
                content,
                null,
                ArticleCategory.valueOf(articleCategory),
                null,
                PriceDto.of(priceValue, deliveryPrice)
        );
    }
}
