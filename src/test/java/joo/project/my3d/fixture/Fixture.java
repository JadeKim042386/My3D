package joo.project.my3d.fixture;

import joo.project.my3d.domain.*;
import joo.project.my3d.domain.constant.*;
import joo.project.my3d.dto.request.ArticleFormRequest;
import joo.project.my3d.dto.request.DimensionOptionRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class Fixture {

    public static Article getArticle(
            String title, String content, ArticleType articleType, ArticleCategory articleCategory) {
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

    public static UserAccount getUserAccount(String email, String userPassword, String nickname, UserRole userRole) {
        return UserAccount.of(
                email,
                userPassword,
                nickname,
                null,
                Address.of(null, null, null),
                userRole,
                Fixture.getUserRefreshToken(),
                Company.of(null, null));
    }

    public static UserAccount getUserAccount() {
        UserAccount userAccount = Fixture.getUserAccount("jk042386@gmail.com", "pw", "Joo", UserRole.USER);
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

    public static MockMultipartFile getMultipartFile() {
        return Fixture.getMultipartFile("Hello, World!");
    }

    public static MockMultipartFile getMultipartFile(String content) {
        String fileName = "test.txt";

        return new MockMultipartFile("modelFile", fileName, "text/plain", content.getBytes(StandardCharsets.UTF_8));
    }

    public static DimensionOption getDimensionOption() {
        return DimensionOption.of("option3");
    }

    public static Dimension getDimension(DimensionOption dimensionOption) {
        return Dimension.of(dimensionOption, "너비", 10.0f, DimUnit.MM);
    }

    public static Alarm getAlarm(UserAccount sender, UserAccount receiver) throws IllegalAccessException {
        Article article = Fixture.getArticle();
        FieldUtils.writeField(article, "id", 1L, true);
        return Alarm.of(AlarmType.NEW_COMMENT, 1L, false, article, sender, receiver);
    }

    public static ArticleFormRequest getArticleFormRequest() throws IllegalAccessException {

        return getArticleFormRequest(Fixture.getMultipartFile());
    }

    public static ArticleFormRequest getArticleFormRequest(MockMultipartFile file) throws IllegalAccessException {
        ArticleFormRequest articleFormRequest = new ArticleFormRequest();
        FieldUtils.writeField(articleFormRequest, "title", "new title", true);
        FieldUtils.writeField(articleFormRequest, "content", "new content", true);
        FieldUtils.writeField(
                articleFormRequest, "dimensionOptions", List.of(Fixture.getDimensionOptionRequest()), true);
        FieldUtils.writeField(articleFormRequest, "modelFile", file, true);
        FieldUtils.writeField(articleFormRequest, "articleCategory", ArticleCategory.ARCHITECTURE.toString(), true);
        return articleFormRequest;
    }

    public static DimensionOptionRequest getDimensionOptionRequest() throws IllegalAccessException {
        DimensionOptionRequest dimensionOptionRequest = new DimensionOptionRequest();
        FieldUtils.writeField(dimensionOptionRequest, "optionName", "option123", true);
        return dimensionOptionRequest;
    }

    public static UserRefreshToken getUserRefreshToken() {
        UserRefreshToken refreshToken = UserRefreshToken.of("refreshToken");
        try {
            FieldUtils.writeField(refreshToken, "id", 1L, true);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return refreshToken;
    }
}
