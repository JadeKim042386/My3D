package joo.project.my3d.dto.request;

import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.dto.ArticleDto;
import joo.project.my3d.dto.ArticleFileDto;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.dto.validation.InCategory;
import joo.project.my3d.dto.validation.MultipartFileSizeValid;
import joo.project.my3d.utils.FileUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

public record ArticleFormRequest(
        @NotBlank
        String title,
        @NotBlank
        String content,
        @MultipartFileSizeValid
        MultipartFile file,
        @InCategory
        String articleCategory
) {

    public static ArticleFormRequest of(String title, String content, MultipartFile file, String articleCategory) {
        return new ArticleFormRequest(title, content, file, articleCategory);
    }

    public ArticleDto toDto(UserAccountDto userAccountDto, ArticleType articleType, String savedFileName) {
        return ArticleDto.of(
                userAccountDto,
                getFileDto(savedFileName),
                title,
                content,
                articleType,
                ArticleCategory.valueOf(articleCategory),
                null
        );
    }

    /**
     * 파일이 업데이트되지 않았다면 FileDto는 추가하지 않음
     */
    public ArticleDto toDto(UserAccountDto userAccountDto, String savedFileName, boolean isUpdated) {
        return ArticleDto.of(
                userAccountDto,
                isUpdated ? getFileDto(savedFileName) : null,
                title,
                content,
                null,
                ArticleCategory.valueOf(articleCategory),
                null
        );
    }

    private ArticleFileDto getFileDto(String savedFileName) {
        long byteSize = file.getSize();
        String originalFileName = file.getOriginalFilename();
        String fileExtension = FileUtils.getExtension(originalFileName);
        return ArticleFileDto.of(
                byteSize,
                originalFileName,
                savedFileName,
                fileExtension
        );
    }
}
