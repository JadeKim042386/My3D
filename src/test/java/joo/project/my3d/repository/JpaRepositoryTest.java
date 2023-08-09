package joo.project.my3d.repository;

import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.config.TestJpaConfig;
import joo.project.my3d.domain.*;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.domain.constant.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
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

        @Autowired private ArticleRepository articleRepository;
        @Autowired private ArticleCommentRepository articleCommentRepository;
        @Autowired private ArticleLikeRepository articleLikeRepository;
        @Autowired private ArticleFileRepository articleFileRepository;

        @DisplayName("게시글 findAll")
        @Test
        void getArticles() {
            // Given

            // When
            List<Article> articles = articleRepository.findAll();
            // Then
            assertThat(articles).isNotNull().hasSize(10);
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

        @DisplayName("게시글&파일 save")
        @Test
        void saveArticle() {
            // Given
            ArticleFile articleFile = Fixture.getArticleFile(10000L, "test.stp", "stp");
            Article article = Fixture.getArticle(articleFile, "title", "content", ArticleType.REQUEST_MODELING);
            long previousCount = articleRepository.count();
            long previousFileCount = articleFileRepository.count();
            log.info("previousCount: {}", previousCount);
            log.info("previousFileCount: {}", previousFileCount);
            // When
            Article savedArticle = articleRepository.save(article);
            ArticleFile savedArticleFile = articleFileRepository.save(articleFile);
            // Then
            long afterCount = articleRepository.count();
            long afterFileCount = articleFileRepository.count();
            log.info("afterCount: {}", afterCount);
            log.info("afterFileCount: {}", afterFileCount);
            assertThat(afterCount).isEqualTo(previousCount + 1);
            assertThat(afterFileCount).isEqualTo(previousFileCount + 1);
            assertThat(savedArticle)
                    .hasFieldOrPropertyWithValue("id", 11L)
                    .hasNoNullFieldsOrPropertiesExcept("articleCategory");
            assertThat(savedArticleFile)
                    .hasFieldOrPropertyWithValue("id", 11L)
                    .hasNoNullFieldsOrPropertiesExcept("article");
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
            assertThat(article.getModifiedBy()).isEqualTo(article.getCreatedBy());
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
            log.info("previousCount: {}", previousCount);
            log.info("previousFileCount: {}", previousFileCount);
            log.info("previousCommentCount: {}", previousCommentCount);
            log.info("previousLikeCount: {}", previousLikeCount);
            // When
            articleRepository.deleteById(articleId);
            // Then
            assertThat(articleRepository.count()).isEqualTo(previousCount - 1);
            assertThat(articleCommentRepository.count()).isEqualTo(previousCommentCount - 1);
            assertThat(articleLikeRepository.count()).isEqualTo(previousLikeCount - 1);
            assertThat(articleFileRepository.count()).isEqualTo(previousFileCount - 1);
        }
    }

    @ActiveProfiles("test")
    @DisplayName("유저 계정 Jpa 테스트")
    @Import(TestJpaConfig.class)
    @DataJpaTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Nested
    public class UserAccountJpaTest {

        @Autowired private UserAccountRepository userAccountRepository;
        @Autowired private ArticleRepository articleRepository;
        @Autowired private ArticleLikeRepository articleLikeRepository;

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
            String userId = "joo";
            // When
            Optional<UserAccount> userAccount = userAccountRepository.findById(userId);
            // Then
            assertThat(userAccount).isNotNull();
        }

        @DisplayName("유저 계정 save")
        @Test
        void saveUserAccount() {
            // Given
            String userId = "joo2";
            UserAccount userAccount = Fixture.getUserAccount(userId, "pw", "joo@gmail.com", "Joo", UserRole.USER);
            long previousCount = userAccountRepository.count();
            log.info("previousCount: {}", previousCount);
            // When
            UserAccount savedUserAccount = userAccountRepository.save(userAccount);
            // Then
            long afterCount = userAccountRepository.count();
            log.info("afterCount: {}", afterCount);
            assertThat(afterCount).isEqualTo(previousCount + 1);
            assertThat(savedUserAccount)
                    .hasFieldOrPropertyWithValue("userId", userId)
                    .hasNoNullFieldsOrProperties();
        }

        @DisplayName("유저 계정 update")
        @Test
        void updateUserAccount() {
            // Given
            String userId = "joo";
            String modified_email = "jk@gmail.com";
            UserAccount userAccount = userAccountRepository.getReferenceById(userId);
            userAccount.setEmail(modified_email);
            LocalDateTime previousModifiedAt = userAccount.getModifiedAt();
            // When
            userAccountRepository.saveAndFlush(userAccount);
            // Then
            assertThat(userAccount).hasFieldOrPropertyWithValue("email", modified_email);
            assertThat(userAccount.getModifiedBy()).isEqualTo(userAccount.getCreatedBy());
            assertThat(userAccount.getModifiedAt()).isNotEqualTo(previousModifiedAt);
        }

        @DisplayName("유저 계정 delete")
        @Test
        void deleteUsrAccount() {
            // Given
            String userId = "joo";
            long previousCount = userAccountRepository.count();
            long previousArticleCount = articleRepository.count();
            long previousLikeCount = articleLikeRepository.count();
            log.info("previousCount: {}", previousCount);
            log.info("previousArticleCount: {}", previousArticleCount);
            log.info("previousLikeCount: {}", previousLikeCount);
            // When
            userAccountRepository.deleteById(userId);
            // Then
            assertThat(userAccountRepository.count()).isEqualTo(previousCount - 1);
            assertThat(articleRepository.count()).isEqualTo(previousArticleCount - 6);
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
        @Autowired private ArticleCommentRepository articleCommentRepository;

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
            ArticleComment articleComment = Fixture.getArticleComment("content");
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
            assertThat(articleComment.getModifiedBy()).isEqualTo(articleComment.getCreatedBy());
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
            assertThat(articleCommentRepository.count()).isEqualTo(previousCount - 1);
        }
    }

    @ActiveProfiles("test")
    @DisplayName("좋아요 Jpa 테스트")
    @Import(TestJpaConfig.class)
    @DataJpaTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Nested
    public class ArticleLikeJpaTest {

        @Autowired private UserAccountRepository userAccountRepository;
        @Autowired private ArticleRepository articleRepository;
        @Autowired private ArticleLikeRepository articleLikeRepository;

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
            ArticleLike articleLike = Fixture.getArticleLike();
            long previousCount = articleLikeRepository.count();
            log.info("previousCount: {}", previousCount);
            // When
            ArticleLike savedArticleLike = articleLikeRepository.saveAndFlush(articleLike);
            // Then
            long afterCount = articleLikeRepository.count();
            log.info("afterCount: {}", afterCount);
            assertThat(afterCount).isEqualTo(previousCount + 1);
            assertThat(savedArticleLike)
                    .hasFieldOrPropertyWithValue("id", 7L)
                    .hasNoNullFieldsOrProperties();
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
        @Autowired private ArticleFileRepository articleFileRepository;

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
}
