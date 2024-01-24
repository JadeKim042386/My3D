package joo.project.my3d.service;

import joo.project.my3d.dto.ArticleCommentDto;

public interface ArticleCommentServiceInterface {
    /**
     * 댓글 저장
     */
    ArticleCommentDto saveComment(ArticleCommentDto dto);

    /**
     * 댓글 삭제
     */
    void deleteComment(Long articleCommentId, String email);
}
