package joo.project.my3d.fixture;

import joo.project.my3d.domain.*;
import joo.project.my3d.domain.constant.*;
import joo.project.my3d.dto.request.ArticleFormRequest;
import joo.project.my3d.dto.request.DimensionOptionRequest;
import joo.project.my3d.utils.CookieUtils;
import joo.project.my3d.utils.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.mock.web.MockMultipartFile;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class Fixture {

    public static Article getArticle(String title, String content, ArticleType articleType, ArticleCategory articleCategory) {
        UserAccount userAccount = Fixture.getUserAccount();
        ArticleFile articleFile = Fixture.getArticleFile();
        return Article.of(userAccount, title, content, articleType, articleCategory, articleFile);
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

    public static ArticleLike getArticleLike(UserAccount userAccount) {
        Article article = Fixture.getArticle();
        return ArticleLike.of(userAccount, article);
    }

    public static UserAccount getUserAccount(String email, String userPassword, String nickname, boolean signUp, UserRole userRole) {
        return UserAccount.of(email, userPassword, nickname, null, Address.of(null, null, null), signUp, userRole, Company.of(null, null));
    }

    public static UserAccount getUserAccount() {
        UserAccount userAccount = Fixture.getUserAccount("jk042386@gmail.com", "pw", "Joo", true, UserRole.USER);
        try {
            FieldUtils.writeField(userAccount, "id", 1L, true);
        } catch (IllegalAccessException e) {
            log.error("UserAccount에 id를 추가할 수 없습니다.");
        }

        return userAccount;
    }

    public static ArticleFile getArticleFile() {
        DimensionOption dimensionOption = Fixture.getDimensionOption();
        return ArticleFile.of(5555L, "test.stp", "uuid.stp", "stp", dimensionOption);
    }

    public static MockMultipartFile getMultipartFile() throws IOException {
        return Fixture.getMultipartFile("Hello, World!");
    }

    public static MockMultipartFile getMultipartFile(String content) {
        String fileName = "test.txt";

        return new MockMultipartFile(
                "modelFile",
                fileName,
                "text/plain",
                content.getBytes(StandardCharsets.UTF_8)
        );
    }

    public static DimensionOption getDimensionOption() {
        return DimensionOption.of("option3");
    }

    public static Dimension getDimension(DimensionOption dimensionOption) {
        return Dimension.of(dimensionOption, "너비", 10.0f, DimUnit.MM);
    }

    public static Company getCompany() {
        return Company.of("company", "homepage");
    }

    public static Alarm getAlarm(UserAccount userAccount) {
        return Alarm.of(AlarmType.NEW_COMMENT_ON_POST, "a@gmail.com", 1L, false, userAccount);
    }

    public static ArticleFormRequest getArticleFormRequest() throws IOException, IllegalAccessException {

        return getArticleFormRequest(Fixture.getMultipartFile());
    }

    public static ArticleFormRequest getArticleFormRequest(MockMultipartFile file) throws IllegalAccessException {
        ArticleFormRequest articleFormRequest = new ArticleFormRequest();
        FieldUtils.writeField(articleFormRequest, "dimensionOptions", List.of(Fixture.getDimensionOptionRequest()), true);
        FieldUtils.writeField(articleFormRequest, "modelFile", file, true);
        return articleFormRequest;
    }

    public static DimensionOptionRequest getDimensionOptionRequest() {
        return DimensionOptionRequest.of("option123");
    }

    public static Cookie getCookie() {

        return CookieUtils.createCookie(
                "token",
                JwtTokenUtils.generateToken(
                        "jooUser@gmail.com",
                        "jooUser",
                        "aaaaagaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                        100000L
                ),
                100,
                "/"
        );
    }
}
