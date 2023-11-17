package joo.project.my3d.dto.response;

public record ArticleLikeResponse(
        Integer likeCount
) {

    public static ArticleLikeResponse of(Integer likeCount) {
        return new ArticleLikeResponse(likeCount);
    }
}
