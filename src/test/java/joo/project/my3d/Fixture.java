package joo.project.my3d;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.ArticleComment;
import joo.project.my3d.domain.ArticleLike;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.ArticleType;

public class Fixture {
    public static Article getArticle(UserAccount userAccount, String title, String content, ArticleType articleType) {
        return Article.of(userAccount, title, content, articleType);
    }

    public static ArticleComment getArticleComment(Article article, String content) {
        return ArticleComment.of(article, content);
    }

    public static ArticleLike getArticleLike(UserAccount userAccount, Article article) {
        return ArticleLike.of(userAccount, article);
    }

    public static UserAccount getUserAccount(String userId, String userPassword, String email, String nickname) {
        return UserAccount.of(userId, userPassword, email, nickname);
    }
}
