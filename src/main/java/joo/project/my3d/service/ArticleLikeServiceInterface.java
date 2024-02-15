package joo.project.my3d.service;

public interface ArticleLikeServiceInterface {
    /**
     * 좋아요 추가 여부 조회
     */
    boolean addedLike(Long articleId, Long userAccountId);

    /**
     * 특정 게시글의 좋아요 수 조회
     */
    int getLikeCountByArticleId(Long articleId);

    /**
     * 좋아요 추가
     */
    int addArticleLike(Long articleId, Long userAccountId);

    /**
     * 좋아요 삭제
     */
    int deleteArticleLike(Long articleId, Long userAccountId);
}
