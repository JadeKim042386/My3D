package joo.project.my3d.dto;

import joo.project.my3d.domain.ArticleFile;

public record ArticleFileWithDimensionDto(
        Long id,
        Long byteSize,
        String originalFileName,
        String fileName,
        String fileExtension,
        DimensionOptionWithDimensionDto dimensionOption) {
    public static ArticleFileWithDimensionDto of(
            Long id,
            Long byteSize,
            String originalFileName,
            String fileName,
            String fileExtension,
            DimensionOptionWithDimensionDto dimensionOptionWithDimensionDto) {
        return new ArticleFileWithDimensionDto(
                id, byteSize, originalFileName, fileName, fileExtension, dimensionOptionWithDimensionDto);
    }

    public static ArticleFileWithDimensionDto of(
            Long byteSize,
            String originalFileName,
            String fileName,
            String fileExtension,
            DimensionOptionWithDimensionDto dimensionOptionWithDimensionDto) {
        return new ArticleFileWithDimensionDto(
                null, byteSize, originalFileName, fileName, fileExtension, dimensionOptionWithDimensionDto);
    }

    public static ArticleFileWithDimensionDto from(ArticleFile articleFile) {
        return ArticleFileWithDimensionDto.of(
                articleFile.getId(),
                articleFile.getByteSize(),
                articleFile.getOriginalFileName(),
                articleFile.getFileName(),
                articleFile.getFileExtension(),
                DimensionOptionWithDimensionDto.from(articleFile.getDimensionOption()));
    }

    public ArticleFile toEntity() {
        return ArticleFile.of(byteSize, originalFileName, fileName, fileExtension, dimensionOption.toEntity());
    }
}
