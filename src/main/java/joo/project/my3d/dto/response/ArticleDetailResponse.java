package joo.project.my3d.dto.response;

import joo.project.my3d.dto.ArticleWithCommentsDto;

public record ArticleDetailResponse(ArticleWithCommentsDto article, boolean addedLike) {
    public static ArticleDetailResponse of(ArticleWithCommentsDto article, boolean addedLike) {
        return new ArticleDetailResponse(article, addedLike);
    }
}
