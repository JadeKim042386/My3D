package joo.project.my3d.service;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.ArticleLike;
import joo.project.my3d.domain.UserAccount;
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

    @Transactional
    public void addArticleLike(Long articleId, String userId) {
        Article article = articleRepository.getReferenceById(articleId);
        UserAccount userAccount = userAccountRepository.getReferenceById(userId);
        ArticleLike articleLike = ArticleLike.of(userAccount, article);

        article.addLike();
        articleLikeRepository.save(articleLike);
    }

    @Transactional
    public void deleteArticleLike(Long articleId) {
        Article article = articleRepository.getReferenceById(articleId);

        article.deleteLike();
        articleLikeRepository.deleteByArticleId(articleId);
    }
}
