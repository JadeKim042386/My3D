package joo.project.my3d.dto.response;

public record ArticleLikeResponse(
        Integer likeCount,
        boolean addedLike
) {

    public static ArticleLikeResponse of(Integer likeCount, boolean addedLike) {
        return new ArticleLikeResponse(likeCount, addedLike);
    }
}
