package joo.project.my3d.dto.response;

import joo.project.my3d.dto.ArticleWithCommentsDto;

public record ArticleDetailResponse(
        ArticleWithCommentsDto article,
        int likeCount,
        boolean addedLike,
        String modelPath
) {
    public static ArticleDetailResponse of(ArticleWithCommentsDto article, int likeCount, boolean addedLike, String modelPath) {
        return new ArticleDetailResponse(article, likeCount, addedLike, modelPath);
    }
}
