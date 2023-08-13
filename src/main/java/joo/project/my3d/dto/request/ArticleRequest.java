package joo.project.my3d.dto.request;

import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.dto.ArticleDto;
import joo.project.my3d.dto.ArticleFileDto;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.utils.FileUtils;
import org.springframework.web.multipart.MultipartFile;

public record ArticleRequest(
        String title,
        String content,
        MultipartFile file,
        String articleCategory
) {

    public static ArticleRequest of(String title, String content, MultipartFile file, String articleCategory) {
        return new ArticleRequest(title, content, file, articleCategory);
    }

    public ArticleDto toDto(UserAccountDto userAccountDto, ArticleType articleType, int likeCount) {
        return ArticleDto.of(
                userAccountDto,
                getFileDto(),
                title,
                content,
                articleType,
                ArticleCategory.valueOf(articleCategory),
                likeCount
        );
    }
    private ArticleFileDto getFileDto() {
        long byteSize = file.getSize();
        String fileName = file.getOriginalFilename();
        String fileExtension = FileUtils.getExtension(fileName);
        return ArticleFileDto.of(
                byteSize,
                fileName,
                fileExtension
        );
    }
}
