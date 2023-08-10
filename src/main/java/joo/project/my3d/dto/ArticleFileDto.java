package joo.project.my3d.dto;

import joo.project.my3d.domain.ArticleFile;

public record ArticleFileDto(
        Long id,
        Long byteSize,
        String fileName,
        String fileExtension
) {
    public static ArticleFileDto of(Long id, Long byteSize, String fineName, String fileExtension) {
        return new ArticleFileDto(id, byteSize, fineName, fileExtension);
    }

    public static ArticleFileDto from(ArticleFile articleFile) {
        return ArticleFileDto.of(
                articleFile.getId(),
                articleFile.getByteSize(),
                articleFile.getFileName(),
                articleFile.getFileExtension()
        );
    }

    public ArticleFile toEntity() {
        return ArticleFile.of(
                byteSize,
                fileName,
                fileExtension
        );
    }
}
