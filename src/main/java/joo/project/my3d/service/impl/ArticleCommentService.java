package joo.project.my3d.service.impl;

import joo.project.my3d.domain.ArticleComment;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.dto.ArticleCommentDto;
import joo.project.my3d.exception.CommentException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.repository.ArticleCommentRepository;
import joo.project.my3d.repository.ArticleRepository;
import joo.project.my3d.service.AlarmServiceInterface;
import joo.project.my3d.service.ArticleCommentServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ArticleCommentService implements ArticleCommentServiceInterface {

    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleRepository articleRepository;
    private final AlarmServiceInterface<SseEmitter> alarmService;

    /**
     * @param sender 댓글 작성자
     * @param receiver 게시글 작성자 (알람을 받을 대상)
     * @throws CommentException 댓글 저장에 필요한 게시글, 유저 정보를 찾을 수 없거나 저장에 실패했을 경우 발생하는 예외
     */
    @Override
    public ArticleCommentDto saveComment(ArticleCommentDto dto, UserAccount sender, UserAccount receiver) {
        try {
            ArticleComment articleComment = dto.toEntity(articleRepository.getReferenceById(dto.articleId()), sender);
            if (dto.parentCommentId() != null) {
                ArticleComment parentComment = articleCommentRepository.getReferenceById(dto.parentCommentId());
                parentComment.addChildComment(articleComment);
            } else {
                articleCommentRepository.save(articleComment);
            }
            alarmService.send(articleComment.getId(), sender, receiver);
            return ArticleCommentDto.from(articleComment);
        } catch (EntityNotFoundException e) {
            throw new CommentException(ErrorCode.DATA_FOR_COMMENT_NOT_FOUND, e);
        } catch (IllegalArgumentException e) {
            throw new CommentException(ErrorCode.FAILED_SAVE, e);
        } catch (OptimisticLockingFailureException e) {
            throw new CommentException(ErrorCode.CONFLICT_SAVE, e);
        }
    }

    /**
     * @throws CommentException 댓글 작성자와 삭제 요청자가 다를 경우 또는 삭제에 실패할 경우 발생하는 예외
     */
    @Override
    public void deleteComment(Long articleCommentId, String email) {
        ArticleComment articleComment = articleCommentRepository.getReferenceById(articleCommentId);
        // 작성자와 삭제 요청 유저가 같은지 확인
        if (!articleCommentRepository.existsByIdAndUserAccount_Email(articleCommentId, email)) {
            log.error(
                    "작성자와 삭제 요청 유저가 다릅니다. 작성자: {} - 삭제 요청: {}",
                    articleComment.getUserAccount().getEmail(),
                    email);
            throw new CommentException(ErrorCode.NOT_WRITER);
        }

        try {
            articleCommentRepository.deleteById(articleCommentId);
        } catch (IllegalArgumentException e) {
            throw new CommentException(ErrorCode.FAILED_DELETE, e);
        }
    }
}
