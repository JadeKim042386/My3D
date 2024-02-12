package joo.project.my3d.dto.response;

public record ArticleLikeResponse(int likeCount) {
    public static ArticleLikeResponse of(int likeCount) {
        return new ArticleLikeResponse(likeCount);
    }
}
