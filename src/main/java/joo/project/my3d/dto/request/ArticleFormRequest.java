package joo.project.my3d.dto.request;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.ArticleFile;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.dto.validation.InCategory;
import joo.project.my3d.dto.validation.MultipartFileSizeValid;
import joo.project.my3d.utils.FileUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ArticleFormRequest {
    private Long id;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotNull
    @Size(min=1, max=1, message = "상품 옵션 1개만 추가해주세요")
    private final List<DimensionOptionRequest> dimensionOptions = new ArrayList<>();
    @MultipartFileSizeValid
    private MultipartFile modelFile;
    @InCategory
    private String articleCategory;

    /**
     * 게시글 등록시 사용
     */
    public Article toArticleEntity(UserAccount userAccount, ArticleType articleType) {
        return Article.of(
                userAccount,
                toArticleFileEntity(),
                title,
                content,
                articleType,
                ArticleCategory.valueOf(articleCategory)
        );
    }

    public ArticleFile toArticleFileEntity() {
        String originalFileName = modelFile.getOriginalFilename();
        String extension = FileUtils.getExtension(originalFileName);

        return ArticleFile.of(
                modelFile.getSize(),
                originalFileName,
                UUID.randomUUID() + "." + extension,
                extension,
                dimensionOptions.get(0).toDimensionOptionEntity()
        );
    }
}
