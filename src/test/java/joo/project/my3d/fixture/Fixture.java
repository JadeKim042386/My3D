package joo.project.my3d.fixture;

import joo.project.my3d.domain.*;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.domain.constant.UserRole;

public class Fixture {

    public static Article getArticle(ArticleFile articleFile, String title, String content, ArticleType articleType) {
        UserAccount userAccount = Fixture.getUserAccount("joo", "pw", "joo@gmail.com", "Joo", UserRole.USER);
        return Article.of(userAccount, articleFile, title, content, articleType);
    }

    public static Article getArticle(UserAccount userAccount, String title, String content, ArticleType articleType) {
        ArticleFile articleFile = Fixture.getArticleFile(5555L, "test.stp", "stp");
        return Article.of(userAccount, articleFile, title, content, articleType);
    }

    public static Article getArticle(String title, String content, ArticleType articleType) {
        UserAccount userAccount = Fixture.getUserAccount("joo", "pw", "joo@gmail.com", "Joo", UserRole.USER);
        ArticleFile articleFile = Fixture.getArticleFile(5555L, "test.stp", "stp");
        return Article.of(userAccount, articleFile, title, content, articleType);
    }

    public static ArticleComment getArticleComment(String content) {
        UserAccount userAccount = Fixture.getUserAccount("joo", "pw", "joo@gmail.com", "Joo", UserRole.USER);
        Article article = Fixture.getArticle("title", "content", ArticleType.MODEL);
        return ArticleComment.of(userAccount, article, content);
    }

    public static ArticleLike getArticleLike() {
        UserAccount userAccount = Fixture.getUserAccount("joo", "pw", "joo@gmail.com", "Joo", UserRole.USER);
        Article article = Fixture.getArticle(userAccount, "title", "content", ArticleType.MODEL);
        return ArticleLike.of(userAccount, article);
    }

    public static UserAccount getUserAccount(String userId, String userPassword, String email, String nickname, UserRole userRole) {
        return UserAccount.of(userId, userPassword, email, nickname, userRole);
    }

    public static ArticleFile getArticleFile(Long byteSize, String fileName, String fileExtension) {
        return ArticleFile.of(byteSize, fileName, fileExtension);
    }
}
