package joo.project.my3d.dto;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.ArticleFile;

public record ArticleFileDto(
        Long id,
        Long articleId,
        Long byteSize,
        String originalFileName,
        String fileName,
        String fileExtension
) {
    public static ArticleFileDto of(Long id, Long articleId, Long byteSize, String originalFileName, String fineName, String fileExtension) {
        return new ArticleFileDto(id, articleId, byteSize, originalFileName, fineName, fileExtension);
    }

    public static ArticleFileDto of(Long articleId, Long byteSize, String originalFileName, String fineName, String fileExtension) {
        return new ArticleFileDto(null, articleId, byteSize, originalFileName, fineName, fileExtension);
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

    public ArticleFile toEntity(Article article) {
        return ArticleFile.of(
                article,
                byteSize,
                originalFileName,
                fileName,
                fileExtension
        );
    }
}
