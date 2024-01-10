package joo.project.my3d.service;

import joo.project.my3d.domain.Alarm;
import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.ArticleComment;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.AlarmType;
import joo.project.my3d.dto.ArticleCommentDto;
import joo.project.my3d.exception.CommentException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.repository.AlarmRepository;
import joo.project.my3d.repository.ArticleCommentRepository;
import joo.project.my3d.repository.ArticleRepository;
import joo.project.my3d.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleCommentService {

    private final ArticleCommentRepository articleCommentRepository;
    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;
    private final AlarmRepository alarmRepository;
    private final AlarmService alarmService;

    public List<ArticleCommentDto> getComments(Long articleId) {
        return articleCommentRepository.findByArticleId(articleId)
                .stream().map(ArticleCommentDto::from)
                .toList();
    }

    /**
     * @throws CommentException 댓글 저장에 필요한 게시글, 유저 정보를 찾을 수 없거나 저장에 실패했을 경우 발생하는 예외
     */
    @Transactional
    public void saveComment(ArticleCommentDto dto) {
        try{
            Article article = articleRepository.getReferenceById(dto.articleId());
            UserAccount userAccount = userAccountRepository.getReferenceByEmail(dto.email());
            ArticleComment articleComment = dto.toEntity(article, userAccount);
            if (dto.parentCommentId() != null) {
                ArticleComment parentComment = articleCommentRepository.getReferenceById(dto.parentCommentId());
                parentComment.addChildComment(articleComment);
            } else {
                articleCommentRepository.save(articleComment);
            }
            Alarm alarm = alarmRepository.save(
                    Alarm.of(
                            AlarmType.NEW_COMMENT_ON_POST,
                            userAccount.getNickname(),
                            article.getId(),
                            false,
                            article.getUserAccount()
                    )
            );
            alarmService.send(article.getUserAccount().getEmail(), alarm.getId());
        } catch (EntityNotFoundException e) {
            throw new CommentException(ErrorCode.DATA_FOR_COMMENT_NOT_FOUND, e);
        } catch (IllegalArgumentException e) {
            throw new CommentException(ErrorCode.FAILED_SAVE, e);
        } catch (OptimisticLockingFailureException e) {
            throw new CommentException(ErrorCode.CONFLICT_SAVE, e);
        }
    }

    /**
     * @throws CommentException 수정하고자하는 댓글이 존재하지 않을 경우 발생하는 예외
     */
    @Transactional
    public void updateComment(ArticleCommentDto dto) {
        try {
            ArticleComment articleComment = articleCommentRepository.getReferenceById(dto.id());
            if (dto.content() != null) {
                articleComment.setContent(dto.content());
            }
        } catch (EntityNotFoundException e) {
            throw new CommentException(ErrorCode.COMMENT_NOT_FOUND, e);
        }
    }

    /**
     * @throws CommentException 댓글 작성자와 삭제 요청자가 다를 경우 또는 삭제에 실패할 경우 발생하는 예외
     */
    @Transactional
    public void deleteComment(Long articleCommentId, String email) {
        ArticleComment articleComment = articleCommentRepository.getReferenceById(articleCommentId);
        //작성자와 삭제 요청 유저가 같은지 확인
        if (articleComment.getUserAccount().getEmail().equals(email)) {
            try {
                articleCommentRepository.deleteById(articleCommentId);
            } catch (IllegalArgumentException e) {
                throw new CommentException(ErrorCode.FAILED_DELETE, e);
            }
        } else {
            log.error("작성자와 삭제 요청 유저가 다릅니다. 작성자: {} - 삭제 요청: {}", articleComment.getUserAccount().getEmail(), email);
            throw new CommentException(ErrorCode.NOT_WRITER);
        }
    }
}
