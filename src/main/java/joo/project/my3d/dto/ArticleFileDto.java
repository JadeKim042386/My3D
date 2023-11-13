package joo.project.my3d.dto;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.ArticleFile;

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

    public ArticleFile toEntity() {
        return ArticleFile.of(
                byteSize,
                originalFileName,
                fileName,
                fileExtension
        );
    }

    public boolean isModelFile() {
        return List.of("stp", "stl").contains(fileExtension);
    }

    public boolean isImageFile() {
        return !List.of("stp", "stl").contains(fileExtension);
    }
}
