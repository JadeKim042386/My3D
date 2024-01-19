package joo.project.my3d.service;

import joo.project.my3d.domain.ArticleLike;
import joo.project.my3d.exception.ArticleLikeException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.repository.ArticleLikeRepository;
import joo.project.my3d.repository.ArticleRepository;
import joo.project.my3d.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
//TODO: 트랜잭션이 불필요해보임
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleLikeService {

    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;
    private final ArticleLikeRepository articleLikeRepository;

    /**
     * @throws ArticleLikeException 게시글을 찾을 수 없거나 좋아요를 추가할 수 없을시 발생하는 예외
     * @return 좋아요 추가 후 반영된 게시글의 좋아요 총 개수
     */
    @Transactional
    public int addArticleLike(Long articleId, String email) {
        try {
            articleLikeRepository.save(
                    ArticleLike.of(
                            userAccountRepository.getReferenceByEmail(email),
                            articleRepository.getReferenceById(articleId)
                    )
            );
            return getLikeCount(articleId);
        } catch (EntityNotFoundException e) {
            throw new ArticleLikeException(ErrorCode.ARTICLE_NOT_FOUND, e);
        } catch (IllegalArgumentException e) {
            throw new ArticleLikeException(ErrorCode.FAILED_SAVE, e);
        } catch (OptimisticLockingFailureException e) {
            throw new ArticleLikeException(ErrorCode.CONFLICT_SAVE, e);
        }
    }

    /**
     * @return 좋아요 해제 후 반영된 게시글의 좋아요 총 개수
     */
    @Transactional
    public int deleteArticleLike(Long articleId, String email) {
        try {
            articleLikeRepository.deleteByArticleIdAndUserAccount_Email(articleId, email);
            return getLikeCount(articleId);
        } catch (EntityNotFoundException e) {
            throw new ArticleLikeException(ErrorCode.FAILED_DELETE, e);
        }
    }

    private int getLikeCount(Long articleId) {
        return articleLikeRepository.countByArticleId(articleId);
    }
}
