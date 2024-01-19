package joo.project.my3d.dto.response;

import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.dto.ArticlePreviewDto;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class PagedResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;

    public PagedResponse(List<T> content, int page, int size, long totalElements, int totalPages, boolean first, boolean last) {
        setContent(content);
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
    }

    public static PagedResponse fromArticlePreview(Page<ArticlePreviewDto> content) {
        return new PagedResponse(
                content.filter(articleDto -> articleDto.articleType() == ArticleType.MODEL).toList(),
                content.getNumber(),
                content.getSize(),
                content.getTotalElements(),
                content.getTotalPages(),
                content.isFirst(),
                content.isLast()
            );
    }

    public List<T> getContent() {
        return content == null ? null : new ArrayList<>(content);
    }

    public final void setContent(List<T> content) {
        if (content == null) {
            this.content = null;
        } else {
            this.content = Collections.unmodifiableList(content);
        }
    }
}
