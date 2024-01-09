package joo.project.my3d.dto.response;

import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.dto.ArticlePreviewDto;
import org.springframework.data.domain.Page;

import java.util.List;

public record ArticlePreviewResponse(
        Page<ArticlePreviewDto> articles,
        String modelPath,
        ArticleCategory[] categories,
        List<Integer> barNumbers
) {
    public static ArticlePreviewResponse of(Page<ArticlePreviewDto> articles, String modelPath, ArticleCategory[] categories, List<Integer> barNumbers) {
        return new ArticlePreviewResponse(articles, modelPath, categories, barNumbers);
    }
}
