package joo.project.my3d.fixture;

import joo.project.my3d.domain.*;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.domain.constant.DimUnit;
import joo.project.my3d.domain.constant.UserRole;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Fixture {

    public static Article getArticle(String title, String summary, String content, ArticleType articleType, ArticleCategory articleCategory) {
        UserAccount userAccount = Fixture.getUserAccount();
        Price price = Fixture.getPrice();
        return Article.of(userAccount, title, summary, content, articleType, articleCategory, price);
    }

    public static Article getArticle(UserAccount userAccount, String title, String summary, String content, ArticleType articleType, ArticleCategory articleCategory) {
        Price price = Fixture.getPrice();
        return Article.of(userAccount, summary, title, content, articleType, articleCategory, price);
    }

    public static Article getArticle() {
        return Fixture.getArticle("title", "summary", "content", ArticleType.MODEL, ArticleCategory.ARCHITECTURE);
    }

    public static ArticleComment getArticleComment(String content) {
        UserAccount userAccount = Fixture.getUserAccount();
        Article article = Fixture.getArticle();
        return ArticleComment.of(userAccount, article, content);
    }

    public static ArticleLike getArticleLike() {
        UserAccount userAccount = Fixture.getUserAccount();
        Article article = Fixture.getArticle();
        return ArticleLike.of(userAccount, article);
    }

    public static ArticleLike getArticleLike(UserAccount userAccount) {
        Article article = Fixture.getArticle();
        return ArticleLike.of(userAccount, article);
    }

    public static UserAccount getUserAccount(String email, String userPassword, String nickname, boolean signUp, UserRole userRole) {
        return UserAccount.of(email, userPassword, nickname, null, Address.of(null, null, null), signUp, userRole, Company.of(null, null));
    }

    public static UserAccount getUserAccount() {
        return Fixture.getUserAccount("jk042386@gmail.com", "pw", "Joo", true, UserRole.USER);
    }

    public static ArticleFile getArticleFile(Article article) {
        return ArticleFile.of(article, 5555L, "test.stp", "uuid.stp", "stp");
    }

    public static MockMultipartFile getMultipartFile() throws IOException {
        return Fixture.getMultipartFile("Hello, World!");
    }

    public static MockMultipartFile getMultipartFile(String content) throws IOException {
        String fileName = "test.txt";

        return new MockMultipartFile(
                fileName,
                fileName,
                "text/plain",
                content.getBytes(StandardCharsets.UTF_8)
        );
    }

    public static GoodOption getGoodOption(Article article) {
        return GoodOption.of(article, "option3", 3000, "LSA", "lesin");
    }

    public static Price getPrice() {
        return Price.of(10000, 3000);
    }

    public static Dimension getDimension(Article article) {
        return Dimension.of(article, "너비", 10.0f, DimUnit.MM);
    }
}
