package joo.project.my3d.service;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.ArticleLike;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.exception.AlarmException;
import joo.project.my3d.repository.ArticleLikeRepository;
import joo.project.my3d.repository.ArticleRepository;
import joo.project.my3d.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleLikeService {

    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;
    private final ArticleLikeRepository articleLikeRepository;

    /**
     * @throws IllegalArgumentException 좋아요 또는 알람 저장 실패시 발생하는 예외
     * @throws AlarmException 알람 전송에 실패할 경우 발생하는 예외
     * @return 좋아요 추가 후 반영된 게시글의 좋아요 총 개수
     */
    @Transactional
    public int addArticleLike(Long articleId, String userId) {
        Article article = articleRepository.getReferenceById(articleId);
        UserAccount userAccount = userAccountRepository.getReferenceByEmail(userId);
        ArticleLike articleLike = ArticleLike.of(userAccount, article);

        article.addLike();
        articleLikeRepository.save(articleLike);

        return article.getLikeCount();
    }

    /**
     * @return 좋아요 해제 후 반영된 게시글의 좋아요 총 개수
     */
    @Transactional
    public int deleteArticleLike(Long articleId) {
        Article article = articleRepository.getReferenceById(articleId);

        article.deleteLike();
        articleLikeRepository.deleteByArticleId(articleId);

        return article.getLikeCount();
    }
}
