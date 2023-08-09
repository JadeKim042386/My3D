package joo.project.my3d.dto;

public record ArticleFileDto(
        Long id,
        Long byteSize,
        String fineName,
        String fileExtension
) {
    public static ArticleFileDto of(Long id, Long byteSize, String fineName, String fileExtension) {
        return new ArticleFileDto(id, byteSize, fineName, fileExtension);
    }
}
