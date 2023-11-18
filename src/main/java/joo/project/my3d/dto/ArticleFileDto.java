package joo.project.my3d.dto;

import joo.project.my3d.domain.ArticleFile;
import joo.project.my3d.domain.DimensionOption;

import java.util.List;

public record ArticleFileDto(
        Long id,
        Long byteSize,
        String originalFileName,
        String fileName,
        String fileExtension
) {
    public static ArticleFileDto of(Long id, Long byteSize, String originalFileName, String fineName, String fileExtension) {
        return new ArticleFileDto(id, byteSize, originalFileName, fineName, fileExtension);
    }

    public static ArticleFileDto of(Long byteSize, String originalFileName, String fineName, String fileExtension) {
        return new ArticleFileDto(null, byteSize, originalFileName, fineName, fileExtension);
    }

    public static ArticleFileDto from(ArticleFile articleFile) {
        return ArticleFileDto.of(
                articleFile.getId(),
                articleFile.getByteSize(),
                articleFile.getOriginalFileName(),
                articleFile.getFileName(),
                articleFile.getFileExtension()
        );
    }

    public ArticleFile toEntity(DimensionOption dimensionOption) {
        return ArticleFile.of(
                byteSize,
                originalFileName,
                fileName,
                fileExtension,
                dimensionOption
        );
    }

    public boolean isModelFile() {
        return List.of("stp", "stl").contains(fileExtension);
    }
}
