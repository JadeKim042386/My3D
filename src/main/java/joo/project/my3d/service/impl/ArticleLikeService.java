package joo.project.my3d.service.impl;

import joo.project.my3d.domain.ArticleLike;
import joo.project.my3d.exception.ArticleLikeException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.repository.ArticleLikeRepository;
import joo.project.my3d.repository.ArticleRepository;
import joo.project.my3d.repository.UserAccountRepository;
import joo.project.my3d.service.ArticleLikeServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleLikeService implements ArticleLikeServiceInterface {

    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;
    private final ArticleLikeRepository articleLikeRepository;

    @Override
    public boolean addedLike(Long articleId, String email) {
        return articleLikeRepository.existsByArticleIdAndUserAccount_Email(articleId, email);
    }

    @Override
    public int getLikeCountByArticleId(Long articleId) {
        return articleLikeRepository.countByArticleId(articleId);
    }

    /**
     * @throws ArticleLikeException 게시글을 찾을 수 없거나 좋아요를 추가할 수 없을시 발생하는 예외
     */
    @Transactional
    @Override
    public int addArticleLike(Long articleId, String email) {
        try {
            // TODO: 이미 좋아요를 눌렀는지 확인 (중복 요청 확인)
            // TODO: 게시글 작성자는 좋아요를 요청할 수 없음
            articleLikeRepository.save(ArticleLike.of(
                    // TODO: userAccount과의 relation은 필요없을 것 같음
                    userAccountRepository.getReferenceByEmail(email), articleRepository.getReferenceById(articleId)));
            articleRepository.addArticleLikeCount();
            return getLikeCountByArticleId(articleId);
        } catch (EntityNotFoundException e) {
            throw new ArticleLikeException(ErrorCode.ARTICLE_NOT_FOUND, e);
        } catch (IllegalArgumentException e) {
            throw new ArticleLikeException(ErrorCode.FAILED_SAVE, e);
        } catch (OptimisticLockingFailureException e) {
            throw new ArticleLikeException(ErrorCode.CONFLICT_SAVE, e);
        }
    }

    @Transactional
    @Override
    public int deleteArticleLike(Long articleId, String email) {
        try {
            // TODO: 이미 좋아요를 취소했는지 확인 (중복 요청 확인)
            // TODO: 게시글 작성자는 좋아요를 취소할 수 없음
            articleLikeRepository.deleteByArticleIdAndUserAccount_Email(articleId, email);
            articleRepository.deleteArticleLikeCount();
            return getLikeCountByArticleId(articleId);
        } catch (EntityNotFoundException e) {
            throw new ArticleLikeException(ErrorCode.FAILED_DELETE, e);
        }
    }
}
