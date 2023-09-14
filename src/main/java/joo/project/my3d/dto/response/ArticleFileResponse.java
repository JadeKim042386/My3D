package joo.project.my3d.dto.response;

import joo.project.my3d.dto.ArticleFileDto;

public record ArticleFileResponse(
        Long id,
        Long byteSize,
        String originalFilename,
        String fileName,
        String fileExtension
) {
    public static ArticleFileResponse of(Long id, Long byteSize, String originalFileName, String fileName, String fileExtension) {
        return new ArticleFileResponse(id, byteSize, originalFileName, fileName, fileExtension);
    }

    public static ArticleFileResponse from(ArticleFileDto dto) {
        return ArticleFileResponse.of(
                dto.id(),
                dto.byteSize(),
                dto.originalFileName(),
                dto.fileName(),
                dto.fileExtension()
        );
    }
}
