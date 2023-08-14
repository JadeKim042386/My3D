package joo.project.my3d.fixture;

import joo.project.my3d.domain.*;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.domain.constant.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Fixture {

    public static Article getArticle(ArticleFile articleFile, String title, String content, ArticleType articleType, ArticleCategory articleCategory) {
        UserAccount userAccount = Fixture.getUserAccount();
        return Article.of(userAccount, articleFile, title, content, articleType, articleCategory);
    }

    public static Article getArticle(UserAccount userAccount, String title, String content, ArticleType articleType, ArticleCategory articleCategory) {
        ArticleFile articleFile = Fixture.getArticleFile();
        return Article.of(userAccount, articleFile, title, content, articleType, articleCategory);
    }

    public static Article getArticle(String title, String content, ArticleType articleType, ArticleCategory articleCategory) {
        UserAccount userAccount = Fixture.getUserAccount();
        ArticleFile articleFile = Fixture.getArticleFile();
        return Article.of(userAccount, articleFile, title, content, articleType, articleCategory);
    }

    public static Article getArticle() {
        return Fixture.getArticle("title", "content", ArticleType.MODEL, ArticleCategory.ARCHITECTURE);
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

    public static UserAccount getUserAccount(String userId, String userPassword, String email, String nickname, UserRole userRole) {
        return UserAccount.of(userId, userPassword, email, nickname, userRole);
    }

    public static UserAccount getUserAccount() {
        return Fixture.getUserAccount("joo", "pw", "joo@gmail.com", "Joo", UserRole.USER);
    }

    public static ArticleFile getArticleFile(Long byteSize, String originalFileName, String fileName, String fileExtension) {
        return ArticleFile.of(byteSize, originalFileName, fileName, fileExtension);
    }

    public static ArticleFile getArticleFile() {
        return Fixture.getArticleFile(5555L, "test.stp", "uuid.stp", "stp");
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
}
