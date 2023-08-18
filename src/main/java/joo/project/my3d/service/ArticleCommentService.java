package joo.project.my3d.service;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.ArticleComment;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.dto.ArticleCommentDto;
import joo.project.my3d.exception.CommentException;
import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.repository.ArticleCommentRepository;
import joo.project.my3d.repository.ArticleRepository;
import joo.project.my3d.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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


    public List<ArticleCommentDto> getComments(Long articleId) {
        return articleCommentRepository.findByArticleId(articleId)
                .stream().map(ArticleCommentDto::from)
                .toList();
    }

    @Transactional
    public void saveComment(ArticleCommentDto dto) {
        try{
            Article article = articleRepository.getReferenceById(dto.articleId());
            UserAccount userAccount = userAccountRepository.getReferenceById(dto.userAccountDto().email());
            ArticleComment articleComment = dto.toEntity(article, userAccount);
            if (dto.parentCommentId() != null) {
                ArticleComment parentComment = articleCommentRepository.getReferenceById(dto.parentCommentId());
                parentComment.addChildComment(articleComment);
            } else {
                articleCommentRepository.save(articleComment);
            }
        } catch (EntityNotFoundException e) {
            log.warn("댓글 저장 실패! - {}", new CommentException(ErrorCode.DATA_FOR_COMMENT_NOT_FOUND));
        }
    }

    @Transactional
    public void updateComment(ArticleCommentDto dto) {
        try {
            ArticleComment articleComment = articleCommentRepository.getReferenceById(dto.id());
            if (dto.content() != null) {
                articleComment.setContent(dto.content());
            }
        } catch (EntityNotFoundException e) {
            log.warn("댓글 수정 실패! - dto: {} {}", dto, new CommentException(ErrorCode.COMMENT_NOT_FOUND));
        }
    }

    @Transactional
    public void deleteComment(Long articleCommentId, String email) {
        ArticleComment articleComment = articleCommentRepository.getReferenceById(articleCommentId);
        //작성자와 삭제 요청 유저가 같은지 확인
        if (articleComment.getUserAccount().getEmail().equals(email)) {
            articleCommentRepository.deleteById(articleCommentId);
        } else {
            log.error("작성자와 삭제 요청 유저가 다릅니다. 작성자: {} - 삭제 요청: {}", articleComment.getUserAccount().getEmail(), email);
            throw new CommentException(ErrorCode.COMMENT_NOT_WRITER);
        }
    }
}
