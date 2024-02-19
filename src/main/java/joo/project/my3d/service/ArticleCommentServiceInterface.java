package joo.project.my3d.service;

import joo.project.my3d.domain.ArticleComment;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.dto.ArticleCommentDto;

public interface ArticleCommentServiceInterface {
    /**
     * 댓글 조회
     */
    ArticleComment searchComment(Long commentId);

    /**
     * 댓글 저장
     */
    ArticleCommentDto saveComment(ArticleCommentDto dto, UserAccount sender, UserAccount receiver);

    /**
     * 댓글 삭제
     */
    void deleteComment(Long articleCommentId, Long userAccountId);
}
