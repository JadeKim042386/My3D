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
    public boolean addedLike(Long articleId, Long userAccountId) {
        return articleLikeRepository.existsByArticleIdAndUserAccountId(articleId, userAccountId);
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
    public int addArticleLike(Long articleId, Long userAccountId) {
        try {
            articleLikeRepository.save(ArticleLike.of(
                    userAccountRepository.getReferenceById(userAccountId),
                    articleRepository.getReferenceById(articleId)));
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
    public int deleteArticleLike(Long articleId, Long userAccountId) {
        try {
            articleLikeRepository.deleteByArticleIdAndUserAccountId(articleId, userAccountId);
            articleRepository.deleteArticleLikeCount();
            return getLikeCountByArticleId(articleId);
        } catch (EntityNotFoundException e) {
            throw new ArticleLikeException(ErrorCode.FAILED_DELETE, e);
        }
    }
}
