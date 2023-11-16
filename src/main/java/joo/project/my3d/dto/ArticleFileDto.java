package joo.project.my3d.dto;

import joo.project.my3d.domain.ArticleFile;

import java.util.List;

public record ArticleFileDto(
        Long id,
        Long byteSize,
        String originalFileName,
        String fileName,
        String fileExtension,
        DimensionOptionWithDimensionDto dimensionOptionWithDimensionDto
) {
    public static ArticleFileDto of(Long id, Long byteSize, String originalFileName, String fineName, String fileExtension, DimensionOptionWithDimensionDto dimensionOptionWithDimensionDto) {
        return new ArticleFileDto(id, byteSize, originalFileName, fineName, fileExtension, dimensionOptionWithDimensionDto);
    }

    public static ArticleFileDto of(Long byteSize, String originalFileName, String fineName, String fileExtension, DimensionOptionWithDimensionDto dimensionOptionWithDimensionDto) {
        return new ArticleFileDto(null, byteSize, originalFileName, fineName, fileExtension, dimensionOptionWithDimensionDto);
    }

    public static ArticleFileDto from(ArticleFile articleFile) {
        return ArticleFileDto.of(
                articleFile.getId(),
                articleFile.getByteSize(),
                articleFile.getOriginalFileName(),
                articleFile.getFileName(),
                articleFile.getFileExtension(),
                DimensionOptionWithDimensionDto.from(articleFile.getDimensionOption())
        );
    }

    public ArticleFile toEntity() {
        return ArticleFile.of(
                byteSize,
                originalFileName,
                fileName,
                fileExtension,
                dimensionOptionWithDimensionDto.toEntity()
        );
    }

    public boolean isModelFile() {
        return List.of("stp", "stl").contains(fileExtension);
    }
}
