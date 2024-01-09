package joo.project.my3d.dto.response;

import joo.project.my3d.dto.ArticleWithCommentsAndLikeCountDto;

public record ArticleDetailResponse(
        ArticleWithCommentsAndLikeCountDto article,
        boolean addedLike,
        String modelPath
) {
    public static ArticleDetailResponse of(ArticleWithCommentsAndLikeCountDto article, boolean addedLike, String modelPath) {
        return new ArticleDetailResponse(article, addedLike, modelPath);
    }
}
