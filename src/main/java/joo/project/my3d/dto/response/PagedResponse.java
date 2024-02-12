package joo.project.my3d.dto.response;

import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.dto.ArticlePreviewDto;
import org.springframework.data.domain.Page;

import java.util.List;

public record PagedResponse<T>(
        List<T> content, int page, int size, long totalElements, int totalPages, boolean first, boolean last) {
    public static PagedResponse<ArticlePreviewDto> fromArticlePreview(Page<ArticlePreviewDto> content) {
        return new PagedResponse<>(
                content.filter(articleDto -> articleDto.articleType() == ArticleType.MODEL)
                        .toList(),
                content.getNumber(),
                content.getSize(),
                content.getTotalElements(),
                content.getTotalPages(),
                content.isFirst(),
                content.isLast());
    }
}
