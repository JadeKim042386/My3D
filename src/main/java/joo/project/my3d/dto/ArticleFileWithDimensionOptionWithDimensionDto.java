package joo.project.my3d.dto;

import joo.project.my3d.domain.ArticleFile;
import joo.project.my3d.dto.request.DimensionOptionRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ArticleFileWithDimensionOptionWithDimensionDto(
        Long id,
        Long byteSize,
        String originalFileName,
        String fileName,
        String fileExtension,
        DimensionOptionWithDimensionDto dimensionOption
) {
    public static ArticleFileWithDimensionOptionWithDimensionDto of(Long id, Long byteSize, String originalFileName, String fineName, String fileExtension, DimensionOptionWithDimensionDto dimensionOptionWithDimensionDto) {
        return new ArticleFileWithDimensionOptionWithDimensionDto(id, byteSize, originalFileName, fineName, fileExtension, dimensionOptionWithDimensionDto);
    }

    public static ArticleFileWithDimensionOptionWithDimensionDto of(Long byteSize, String originalFileName, String fineName, String fileExtension, DimensionOptionWithDimensionDto dimensionOptionWithDimensionDto) {
        return new ArticleFileWithDimensionOptionWithDimensionDto(null, byteSize, originalFileName, fineName, fileExtension, dimensionOptionWithDimensionDto);
    }

    public static ArticleFileWithDimensionOptionWithDimensionDto from(ArticleFile articleFile) {
        return ArticleFileWithDimensionOptionWithDimensionDto.of(
                articleFile.getId(),
                articleFile.getByteSize(),
                articleFile.getOriginalFileName(),
                articleFile.getFileName(),
                articleFile.getFileExtension(),
                DimensionOptionWithDimensionDto.from(articleFile.getDimensionOption())
        );
    }

    public static ArticleFileWithDimensionOptionWithDimensionDto from(MultipartFile file, List<DimensionOptionRequest> dimensionOptions) {
        return ArticleFileWithDimensionOptionWithDimensionDto.of(
                null,
                file.getSize(),
                null,
                null,
                null,
                dimensionOptions.isEmpty() ? null : dimensionOptions.get(0).toWithDimensionDto()
        );
    }

    public ArticleFile toEntity() {
        return ArticleFile.of(
                byteSize,
                originalFileName,
                fileName,
                fileExtension,
                dimensionOption.toEntity()
        );
    }
}
