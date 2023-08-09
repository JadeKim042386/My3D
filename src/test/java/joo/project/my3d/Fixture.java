package joo.project.my3d;

import joo.project.my3d.domain.*;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.domain.constant.UserRole;

public class Fixture {
    public static Article getArticle(UserAccount userAccount, ArticleFile articleFile, String title, String content, ArticleType articleType) {
        return Article.of(userAccount, articleFile, title, content, articleType);
    }

    public static ArticleComment getArticleComment(Article article, String content) {
        return ArticleComment.of(article, content);
    }

    public static ArticleLike getArticleLike(UserAccount userAccount, Article article) {
        return ArticleLike.of(userAccount, article);
    }

    public static UserAccount getUserAccount(String userId, String userPassword, String email, String nickname, UserRole userRole) {
        return UserAccount.of(userId, userPassword, email, nickname, userRole);
    }

    public static ArticleFile getArticleFile(Long byteSize, String fileName, String fileExtension) {
        return ArticleFile.of(byteSize, fileName, fileExtension);
    }
}
