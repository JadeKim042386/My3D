package joo.project.my3d.repository;

import joo.project.my3d.config.TestJpaConfig;
import joo.project.my3d.domain.*;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.DimUnit;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.fixture.Fixture;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@DisplayName("Jpa Repository 테스트")
public class JpaRepositoryTest {

    @ActiveProfiles("test")
    @DisplayName("게시글 Jpa 테스트")
    @Import(TestJpaConfig.class)
    @DataJpaTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Nested
    public class ArticleJpaTest {

        @Autowired
        private ArticleRepository articleRepository;

        @Autowired
        private ArticleCommentRepository articleCommentRepository;

        @Autowired
        private ArticleLikeRepository articleLikeRepository;

        @Autowired
        private ArticleFileRepository articleFileRepository;

        @Autowired
        private DimensionRepository dimensionRepository;

        @Autowired
        private DimensionOptionRepository dimensionOptionRepository;

        @DisplayName("게시글 findAll")
        @Test
        void getArticles() {
            // Given
            Pageable pageable = Pageable.ofSize(9);
            // When
            Page<Article> articles = articleRepository.findAll(pageable);
            // Then
            assertThat(articles).isNotNull().hasSize(9);
        }

        @DisplayName("게시글 findById")
        @Test
        void getArticle() {
            // Given
            Long articleId = 1L;
            // When
            Optional<Article> article = articleRepository.findById(articleId);
            // Then
            assertThat(article).isNotNull();
        }

        @DisplayName("게시글 save")
        @Test
        void saveArticle() {
            // Given
            Article article = Fixture.getArticle();
            long previousCount = articleRepository.count();
            log.info("previousCount: {}", previousCount);
            // When
            Article savedArticle = articleRepository.save(article);
            // Then
            long afterCount = articleRepository.count();
            log.info("afterCount: {}", afterCount);
            assertThat(afterCount).isEqualTo(previousCount + 1);
            assertThat(savedArticle)
                    .hasFieldOrPropertyWithValue("id", 11L)
                    .hasNoNullFieldsOrPropertiesExcept("articleCategory");
        }

        @DisplayName("게시글 update")
        @Test
        void updateArticle() {
            // Given
            Long articleId = 1L;
            String modified_title = "modified_title";
            Article article = articleRepository.getReferenceById(articleId);
            article.setTitle(modified_title);
            LocalDateTime previousModifiedAt = article.getModifiedAt();
            // When
            articleRepository.saveAndFlush(article);
            // Then
            assertThat(article).hasFieldOrPropertyWithValue("title", modified_title);
            assertThat(article.getModifiedAt()).isNotEqualTo(previousModifiedAt);
        }

        @DisplayName("게시글&파일 delete")
        @Test
        void deleteArticle() {
            // Given
            Long articleId = 1L;
            long previousCount = articleRepository.count();
            long previousFileCount = articleFileRepository.count();
            long previousCommentCount = articleCommentRepository.count();
            long previousLikeCount = articleLikeRepository.count();
            long previousDimension = dimensionRepository.count();
            long previousDimensionOption = dimensionOptionRepository.count();
            log.info("previousCount: {}", previousCount);
            log.info("previousFileCount: {}", previousFileCount);
            log.info("previousCommentCount: {}", previousCommentCount);
            log.info("previousLikeCount: {}", previousLikeCount);
            log.info("previousDimension: {}", previousDimension);
            log.info("previousDimensionOption: {}", previousDimensionOption);
            // When
            articleRepository.getReferenceById(articleId).deleteAll();
            articleRepository.deleteById(articleId);
            // Then
            assertThat(articleRepository.count()).isEqualTo(previousCount - 1);
            assertThat(articleCommentRepository.count()).isEqualTo(previousCommentCount - 1);
            assertThat(articleLikeRepository.count()).isEqualTo(previousLikeCount - 1);
            assertThat(articleFileRepository.count()).isEqualTo(previousFileCount - 1);
            assertThat(dimensionRepository.count()).isEqualTo(previousDimension - 1);
            assertThat(dimensionOptionRepository.count()).isEqualTo(previousDimensionOption - 1);
        }
    }

    @ActiveProfiles("test")
    @DisplayName("유저 계정 Jpa 테스트")
    @Import(TestJpaConfig.class)
    @DataJpaTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Nested
    public class UserAccountJpaTest {

        @Autowired
        private UserAccountRepository userAccountRepository;

        @Autowired
        private ArticleRepository articleRepository;

        @Autowired
        private ArticleCommentRepository articleCommentRepository;

        @Autowired
        private ArticleLikeRepository articleLikeRepository;

        @DisplayName("유저 계정 findAll")
        @Test
        void getUserAccounts() {
            // Given

            // When
            List<UserAccount> userAccounts = userAccountRepository.findAll();
            // Then
            assertThat(userAccounts).isNotNull().hasSize(3);
        }

        @DisplayName("유저 계정 findById")
        @Test
        void getUserAccount() {
            // Given
            Long userAccountId = 1L;
            // When
            Optional<UserAccount> userAccount = userAccountRepository.findById(userAccountId);
            // Then
            assertThat(userAccount).isNotNull();
        }

        @DisplayName("유저 계정 save")
        @Test
        void saveUserAccount() {
            // Given
            String email = "joo2@gmail.com";
            UserAccount userAccount = Fixture.getUserAccount(email, "pw", "Joo2", UserRole.USER);
            long previousCount = userAccountRepository.count();
            log.info("previousCount: {}", previousCount);
            // When
            UserAccount savedUserAccount = userAccountRepository.save(userAccount);
            // Then
            long afterCount = userAccountRepository.count();
            log.info("afterCount: {}", afterCount);
            assertThat(afterCount).isEqualTo(previousCount + 1);
            assertThat(savedUserAccount)
                    .hasFieldOrPropertyWithValue("email", email)
                    .hasNoNullFieldsOrPropertiesExcept("phone", "address");
        }

        @DisplayName("유저 계정 update")
        @Test
        void updateUserAccount() {
            // Given
            Long userAccountId = 1L;
            String modified_phone = "01043214321";
            UserAccount userAccount = userAccountRepository.getReferenceById(userAccountId);
            userAccount.setPhone(modified_phone);
            LocalDateTime previousModifiedAt = userAccount.getModifiedAt();
            // When
            userAccountRepository.saveAndFlush(userAccount);
            // Then
            assertThat(userAccount).hasFieldOrPropertyWithValue("phone", modified_phone);
            assertThat(userAccount.getModifiedAt()).isNotEqualTo(previousModifiedAt);
        }

        @DisplayName("유저 계정 delete")
        @Test
        void deleteUsrAccount() {
            // Given
            String email = "jk042386@gmail.com";
            Long userAccountId = 1L;
            long previousCount = userAccountRepository.count();
            long previousArticleCount = articleRepository.count();
            long previousArticleCommentCount = articleCommentRepository.count();
            long previousLikeCount = articleLikeRepository.count();
            log.info("previousCount: {}", previousCount);
            log.info("previousArticleCount: {}", previousArticleCount);
            log.info("previousArticleCommentCount: {}", previousArticleCommentCount);
            log.info("previousLikeCount: {}", previousLikeCount);
            // When
            articleRepository.findAll().stream().filter(article -> article.getUserAccount().getEmail().equals(email)).forEach(Article::deleteAll);
            UserAccount userAccount =
                    userAccountRepository.findById(userAccountId).get();
            userAccount.getArticleComments().clear();
            userAccount.getArticleLikes().clear();
            userAccount.getSenderAlarms().clear();
            userAccount.getReceiverAlarms().clear();
            userAccountRepository.deleteById(userAccountId);
            // Then
            assertThat(userAccountRepository.count()).isEqualTo(previousCount - 1);
            assertThat(articleRepository.count()).isEqualTo(previousArticleCount - 5);
            assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount - 9); // 부모 + 자식 댓글 + 게시글
            assertThat(articleLikeRepository.count()).isEqualTo(previousLikeCount - 3);
        }
    }

    @ActiveProfiles("test")
    @DisplayName("댓글 Jpa 테스트")
    @Import(TestJpaConfig.class)
    @DataJpaTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Nested
    public class ArticleCommentJpaTest {

        @Autowired
        private ArticleCommentRepository articleCommentRepository;

        @Autowired
        private UserAccountRepository userAccountRepository;

        @Autowired
        private ArticleRepository articleRepository;

        @DisplayName("댓글 findAll")
        @Test
        void getArticleComments() {
            // Given

            // When
            List<ArticleComment> articleComments = articleCommentRepository.findAll();
            // Then
            assertThat(articleComments).isNotNull().hasSize(10);
        }

        @DisplayName("댓글 findById")
        @Test
        void getArticleComment() {
            // Given
            Long articleCommentId = 1L;
            // When
            Optional<ArticleComment> articleComment = articleCommentRepository.findById(articleCommentId);
            // Then
            assertThat(articleComment).isNotNull();
        }

        @DisplayName("댓글 save")
        @Test
        void saveArticleComment() {
            // Given
            Long userAccountId = 1L;
            ArticleComment articleComment = ArticleComment.of(
                    userAccountRepository.findById(userAccountId).get(),
                    articleRepository.findById(1L).get(),
                    "content");
            long previousCount = articleCommentRepository.count();
            log.info("previousCount: {}", previousCount);
            // When
            ArticleComment savedArticleComment = articleCommentRepository.saveAndFlush(articleComment);
            // Then
            long afterCount = articleCommentRepository.count();
            log.info("afterCount: {}", afterCount);
            assertThat(afterCount).isEqualTo(previousCount + 1);
            assertThat(savedArticleComment)
                    .hasFieldOrPropertyWithValue("id", 11L)
                    .hasNoNullFieldsOrPropertiesExcept("parentCommentId");
        }

        @DisplayName("댓글 update")
        @Test
        void updateArticleComment() {
            // Given
            Long articleCommentId = 1L;
            String modified_content = "modified_content";
            ArticleComment articleComment = articleCommentRepository.getReferenceById(articleCommentId);
            articleComment.setContent(modified_content);
            LocalDateTime previousModifiedAt = articleComment.getModifiedAt();
            // When
            articleCommentRepository.saveAndFlush(articleComment);
            // Then
            assertThat(articleComment).hasFieldOrPropertyWithValue("content", modified_content);
            assertThat(articleComment.getModifiedAt()).isNotEqualTo(previousModifiedAt);
        }

        @DisplayName("댓글 delete")
        @Test
        void deleteArticleComment() {
            // Given
            Long articleCommentId = 1L;
            long previousCount = articleCommentRepository.count();
            log.info("previousCount: {}", previousCount);
            // When
            articleCommentRepository.deleteById(articleCommentId);
            // Then
            assertThat(articleCommentRepository.count()).isEqualTo(previousCount - 2); // 부모 + 자식 댓글
        }
    }

    @ActiveProfiles("test")
    @DisplayName("좋아요 Jpa 테스트")
    @Import(TestJpaConfig.class)
    @DataJpaTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Nested
    public class ArticleLikeJpaTest {

        @Autowired
        private UserAccountRepository userAccountRepository;

        @Autowired
        private ArticleRepository articleRepository;

        @Autowired
        private ArticleLikeRepository articleLikeRepository;

        @DisplayName("좋아요 findAll")
        @Test
        void getArticleLikes() {
            // Given

            // When
            List<ArticleLike> articleLikes = articleLikeRepository.findAll();
            // Then
            assertThat(articleLikes).isNotNull().hasSize(6);
        }

        @DisplayName("좋아요 findById")
        @Test
        void getArticleLike() {
            // Given
            Long articleLikeId = 1L;
            // When
            Optional<ArticleLike> articleLike = articleLikeRepository.findById(articleLikeId);
            // Then
            assertThat(articleLike).isNotNull();
        }

        @DisplayName("좋아요 save")
        @Test
        void saveArticleLike() {
            // Given
            ArticleLike articleLike = ArticleLike.of(
                    userAccountRepository.findById(1L).get(),
                    articleRepository.findById(3L).get());
            long previousCount = articleLikeRepository.count();
            log.info("previousCount: {}", previousCount);
            // When
            ArticleLike savedArticleLike = articleLikeRepository.saveAndFlush(articleLike);
            // Then
            long afterCount = articleLikeRepository.count();
            log.info("afterCount: {}", afterCount);
            assertThat(afterCount).isEqualTo(previousCount + 1);
            assertThat(savedArticleLike).hasFieldOrPropertyWithValue("id", 7L).hasNoNullFieldsOrProperties();
        }

        @DisplayName("좋아요 delete")
        @Test
        void deleteArticleLike() {
            // Given
            Long articleLikeId = 1L;
            long previousCount = articleLikeRepository.count();
            log.info("previousCount: {}", previousCount);
            // When
            articleLikeRepository.deleteById(articleLikeId);
            // Then
            assertThat(articleLikeRepository.count()).isEqualTo(previousCount - 1);
        }
    }

    @ActiveProfiles("test")
    @DisplayName("파일 Jpa 테스트")
    @Import(TestJpaConfig.class)
    @DataJpaTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Nested
    public class ArticleFileJpaTest {
        @Autowired
        private ArticleRepository articleRepository;

        @Autowired
        private ArticleFileRepository articleFileRepository;

        @Autowired
        private DimensionOptionRepository dimensionOptionRepository;

        @Autowired
        private DimensionRepository dimensionRepository;

        @DisplayName("파일 findAll")
        @Test
        void getArticleFiles() {
            // Given

            // When
            List<ArticleFile> articleFiles = articleFileRepository.findAll();
            // Then
            assertThat(articleFiles).isNotNull().hasSize(10);
        }

        @DisplayName("파일 findById")
        @Test
        void getArticleFile() {
            // Given
            Long articleFileId = 1L;
            // When
            Optional<ArticleFile> articleFile = articleFileRepository.findById(articleFileId);
            // Then
            assertThat(articleFile).isNotNull();
        }
    }

    @ActiveProfiles("test")
    @DisplayName("치수 옵션 Jpa 테스트")
    @Import(TestJpaConfig.class)
    @DataJpaTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Nested
    public class DimensionOptionJpaTest {

        @Autowired
        private ArticleFileRepository articleFileRepository;

        @Autowired
        private DimensionOptionRepository dimensionOptionRepository;

        @DisplayName("치수 옵션 findAll")
        @Test
        void getDimensionOptions() {
            // Given

            // When
            List<DimensionOption> dimensionOptions = dimensionOptionRepository.findAll();
            // Then
            assertThat(dimensionOptions).isNotNull().hasSize(10);
        }

        @DisplayName("치수 옵션 findById")
        @Test
        void getDimensionOption() {
            // Given
            Long dimensionOptionId = 1L;
            // When
            Optional<DimensionOption> dimensionOption = dimensionOptionRepository.findById(dimensionOptionId);
            // Then
            assertThat(dimensionOption).isNotNull();
        }

        @DisplayName("치수 옵션 save")
        @Test
        void saveDimensionOption() {
            // Given
            DimensionOption dimensionOption = Fixture.getDimensionOption();
            long previousCount = dimensionOptionRepository.count();
            log.info("previousCount: {}", previousCount);
            // When
            DimensionOption savedDimensionOption = dimensionOptionRepository.save(dimensionOption);
            // Then
            long afterCount = dimensionOptionRepository.count();
            log.info("afterCount: {}", afterCount);
            assertThat(afterCount).isEqualTo(previousCount + 1);
            assertThat(savedDimensionOption).hasFieldOrPropertyWithValue("id", 11L);
        }

        @DisplayName("치수 옵션 update")
        @Test
        void updateDimensionOption() {
            // Given
            Long dimensionOptionId = 1L;
            DimensionOption dimensionOption = dimensionOptionRepository.getReferenceById(dimensionOptionId);
            dimensionOption.setOptionName("option3");
            // When
            dimensionOptionRepository.saveAndFlush(dimensionOption);
            // Then
            assertThat(dimensionOption.getOptionName()).isEqualTo("option3");
        }
    }

    @ActiveProfiles("test")
    @DisplayName("치수 Jpa 테스트")
    @Import(TestJpaConfig.class)
    @DataJpaTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Nested
    public class DimensionJpaTest {

        @Autowired
        private DimensionRepository dimensionRepository;

        @Autowired
        private DimensionOptionRepository dimensionOptionRepository;

        @DisplayName("치수 findAll")
        @Test
        void getDimensions() {
            // Given

            // When
            List<Dimension> dimensions = dimensionRepository.findAll();
            // Then
            assertThat(dimensions).isNotNull().hasSize(10);
        }

        @DisplayName("치수 findById")
        @Test
        void getDimension() {
            // Given
            Long dimensionId = 1L;
            // When
            Optional<Dimension> dimension = dimensionRepository.findById(dimensionId);
            // Then
            assertThat(dimension).isNotNull();
        }

        @DisplayName("치수 save")
        @Test
        void saveDimensionAddDimensionOption() {
            // Given
            DimensionOption dimensionOption = dimensionOptionRepository.getReferenceById(1L);
            Dimension dimension = Fixture.getDimension(dimensionOption);
            long previousCount = dimensionRepository.count();
            log.info("previousCount: {}", previousCount);
            // When
            dimensionOption.getDimensions().add(dimension);
            // Then
            long afterCount = dimensionRepository.count();
            log.info("afterCount: {}", afterCount);
            assertThat(afterCount).isEqualTo(previousCount + 1);
        }

        @DisplayName("치수 update")
        @Test
        void updateDimension() {
            // Given
            Long dimensionId = 1L;
            DimUnit modified_unit = DimUnit.INCH;
            Dimension dimension = dimensionRepository.getReferenceById(dimensionId);
            dimension.setDimUnit(modified_unit);
            // When
            dimensionRepository.saveAndFlush(dimension);
            // Then
            assertThat(dimension).hasFieldOrPropertyWithValue("dimUnit", modified_unit);
        }

        @DisplayName("치수 delete")
        @Test
        void deleteDimension() {
            // Given
            Long dimensionId = 1L;
            long previousCount = dimensionRepository.count();
            log.info("previousCount: {}", previousCount);
            // When
            dimensionRepository.deleteById(dimensionId);
            // Then
            assertThat(dimensionRepository.count()).isEqualTo(previousCount - 1);
        }
    }

    @ActiveProfiles("test")
    @DisplayName("알람 Jpa 테스트")
    @Import(TestJpaConfig.class)
    @DataJpaTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Nested
    public class AlarmJpaTest {

        @Autowired
        private AlarmRepository alarmRepository;

        @Autowired
        private UserAccountRepository userAccountRepository;

        @DisplayName("알람 findAll")
        @Test
        void getAlarms() {
            // Given

            // When
            List<Alarm> alarms = alarmRepository.findAll();
            // Then
            assertThat(alarms).isNotNull().hasSize(2);
        }

        @DisplayName("알람 findById")
        @Test
        void getAlarm() {
            // Given
            Long alarmId = 1L;
            // When
            Optional<Alarm> alarm = alarmRepository.findById(alarmId);
            // Then
            assertThat(alarm).isNotNull();
        }

        @DisplayName("알람 save")
        @Test
        void saveAlarm() {
            // Given
            Long senderId = 3L;
            Long receiverId = 3L;
            UserAccount sender = userAccountRepository.getReferenceById(senderId);
            UserAccount receiver = userAccountRepository.getReferenceById(receiverId);
            Alarm alarm = Fixture.getAlarm(sender, receiver);
            long previousCount = alarmRepository.count();
            log.info("previousCount: {}", previousCount);
            // When
            Alarm savedAlarm = alarmRepository.save(alarm);
            // Then
            long afterCount = alarmRepository.count();
            log.info("afterCount: {}", afterCount);
            assertThat(afterCount).isEqualTo(previousCount + 1);
            assertThat(savedAlarm).hasFieldOrPropertyWithValue("id", 3L);
        }

        @DisplayName("알람 delete")
        @Test
        void deleteAlarm() {
            // Given
            Long alarmId = 1L;
            long previousCount = alarmRepository.count();
            log.info("previousCount: {}", previousCount);
            // When
            alarmRepository.deleteById(alarmId);
            // Then
            assertThat(alarmRepository.count()).isEqualTo(previousCount - 1);
        }
    }
}
