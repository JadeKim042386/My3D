package joo.project.my3d.repository;

import joo.project.my3d.Fixture;
import joo.project.my3d.config.TestJpaConfig;
import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.ArticleComment;
import joo.project.my3d.domain.ArticleLike;
import joo.project.my3d.domain.UserAccount;
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

        @DisplayName("게시글 save")
        @Test
        void saveArticle() {
            // Given
            UserAccount userAccount = Fixture.getUserAccount("joo", "pw", "joo@gmail.com", "Joo", UserRole.USER);
            Article article = Fixture.getArticle(userAccount, "title", "content", ArticleType.REQUEST_MODELING);
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
            assertThat(article.getModifiedBy()).isEqualTo(article.getCreatedBy());
            assertThat(article.getModifiedAt()).isNotEqualTo(previousModifiedAt);

        }

        @DisplayName("게시글 delete")
        @Test
        void deleteArticle() {
            // Given
            Long articleId = 1L;
            long previousCount = articleRepository.count();
            log.info("previousCount: {}", previousCount);
            // When
            articleRepository.deleteById(articleId);
            // Then
            assertThat(articleRepository.count()).isEqualTo(previousCount - 1);
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
            log.info("previousCount: {}", previousCount);
            // When
            userAccountRepository.deleteById(userId);
            // Then
            assertThat(userAccountRepository.count()).isEqualTo(previousCount - 1);
        }
    }

    @ActiveProfiles("test")
    @DisplayName("댓글 Jpa 테스트")
    @Import(TestJpaConfig.class)
    @DataJpaTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Nested
    public class ArticleCommentJpaTest {

        @Autowired private ArticleRepository articleRepository;
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
            Article article = articleRepository.findById(1L).get();
            ArticleComment articleComment = Fixture.getArticleComment(article, "content");
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
            UserAccount userAccount = userAccountRepository.findById("joo").get();
            Article article = articleRepository.findById(1L).get();
            ArticleLike articleLike = Fixture.getArticleLike(userAccount, article);
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
            Long articleCommentId = 1L;
            long previousCount = articleLikeRepository.count();
            log.info("previousCount: {}", previousCount);
            // When
            articleLikeRepository.deleteById(articleCommentId);
            // Then
            assertThat(articleLikeRepository.count()).isEqualTo(previousCount - 1);
        }
    }
}
