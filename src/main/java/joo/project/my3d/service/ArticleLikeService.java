package joo.project.my3d.service;

import joo.project.my3d.domain.Alarm;
import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.ArticleLike;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.AlarmType;
import joo.project.my3d.repository.AlarmRepository;
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
    private final AlarmRepository alarmRepository;
    private final AlarmService alarmService;

    @Transactional
    public void addArticleLike(Long articleId, String userId) {
        Article article = articleRepository.getReferenceById(articleId);
        UserAccount userAccount = userAccountRepository.getReferenceById(userId);
        ArticleLike articleLike = ArticleLike.of(userAccount, article);

        article.addLike();
        articleLikeRepository.save(articleLike);
        Alarm alarm = alarmRepository.save(
                Alarm.of(
                        AlarmType.NEW_LIKE_ON_POST,
                        userAccount.getNickname(),
                        article.getId(),
                        false,
                        article.getUserAccount()
                )
        );
        alarmService.send(article.getUserAccount().getEmail(), alarm.getId());
    }

    @Transactional
    public void deleteArticleLike(Long articleId) {
        Article article = articleRepository.getReferenceById(articleId);

        article.deleteLike();
        articleLikeRepository.deleteByArticleId(articleId);
    }


}
