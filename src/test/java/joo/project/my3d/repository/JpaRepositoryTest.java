package joo.project.my3d.repository;

import joo.project.my3d.config.TestJpaConfig;
import joo.project.my3d.domain.*;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.DimUnit;
import joo.project.my3d.domain.constant.OrderStatus;
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

        @Autowired private ArticleRepository articleRepository;
        @Autowired private ArticleCommentRepository articleCommentRepository;
        @Autowired private ArticleLikeRepository articleLikeRepository;
        @Autowired private ArticleFileRepository articleFileRepository;
        @Autowired private DimensionRepository dimensionRepository;
        @Autowired private GoodOptionRepository goodOptionRepository;
        @Autowired private PriceRepository priceRepository;

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

        @DisplayName("게시글 findByArticleCategory")
        @Test
        void getArticleByArticleCategory() {
            // Given
            Pageable pageable = Pageable.ofSize(9);
            // When
            Page<Article> articles = articleRepository.findByArticleCategory(ArticleCategory.MUSIC, pageable);
            // Then
            assertThat(articles).hasSize(6);
            assertThat(articles.getContent().get(0).getArticleCategory()).isEqualTo(ArticleCategory.MUSIC);
        }

        @DisplayName("게시글&파일 save")
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
            long previousDimension = dimensionRepository.count();
            long previousGoodOption = goodOptionRepository.count();
            long previousPrice = priceRepository.count();
            log.info("previousCount: {}", previousCount);
            log.info("previousFileCount: {}", previousFileCount);
            log.info("previousCommentCount: {}", previousCommentCount);
            log.info("previousLikeCount: {}", previousLikeCount);
            log.info("previousDimension: {}", previousDimension);
            log.info("previousGoodOption: {}", previousGoodOption);
            log.info("previousPrice: {}", previousPrice);
            // When
            articleCommentRepository.deleteByArticleId(articleId);
            articleLikeRepository.deleteByArticleId(articleId);
            articleRepository.deleteById(articleId);
            // Then
            assertThat(articleRepository.count()).isEqualTo(previousCount - 1);
            assertThat(articleCommentRepository.count()).isEqualTo(previousCommentCount - 1);
            assertThat(articleLikeRepository.count()).isEqualTo(previousLikeCount - 1);
            assertThat(articleFileRepository.count()).isEqualTo(previousFileCount - 1);
            assertThat(dimensionRepository.count()).isEqualTo(previousDimension - 2);
            assertThat(goodOptionRepository.count()).isEqualTo(previousGoodOption - 2);
            assertThat(priceRepository.count()).isEqualTo(previousPrice - 1);
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
        @Autowired private ArticleCommentRepository articleCommentRepository;
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
            UserAccount userAccount = Fixture.getUserAccount(email, "pw", "Joo2", true, UserRole.USER);
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
            assertThat(userAccount.getModifiedBy()).isEqualTo(userAccount.getCreatedBy());
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
            articleRepository.findAllByUserAccount_Email(email)
                    .forEach(article -> {
                        articleCommentRepository.deleteByArticleId(article.getId());
                        articleCommentRepository.deleteByUserAccount_Email(email);
                        articleLikeRepository.deleteByArticleId(article.getId());
                        articleLikeRepository.deleteByUserAccount_Email(email);
                        articleRepository.deleteById(article.getId());
                    });
            userAccountRepository.deleteById(userAccountId);
            // Then
            assertThat(userAccountRepository.count()).isEqualTo(previousCount - 1);
            assertThat(articleRepository.count()).isEqualTo(previousArticleCount - 5);
            assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount - 9); //부모 + 자식 댓글 + 게시글
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
        @Autowired private UserAccountRepository userAccountRepository;
        @Autowired private ArticleRepository articleRepository;

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
            ArticleComment articleComment = ArticleComment.of(userAccountRepository.findById(userAccountId).get(), articleRepository.findById(1L).get(), "content");
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
            assertThat(articleCommentRepository.count()).isEqualTo(previousCount - 2); //부모 + 자식 댓글
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
            Long userAccountId = 1L;
            ArticleLike articleLike = ArticleLike.of(userAccountRepository.findById(userAccountId).get(), articleRepository.findById(1L).get());
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
        @Autowired private ArticleRepository articleRepository;

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
    @DisplayName("상품옵션 Jpa 테스트")
    @Import(TestJpaConfig.class)
    @DataJpaTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Nested
    public class GoodOptionJpaTest {

        @Autowired private GoodOptionRepository goodOptionRepository;
        @Autowired private ArticleRepository articleRepository;

        @DisplayName("상품옵션 findAll")
        @Test
        void getGoodOptions() {
            // Given

            // When
            List<GoodOption> goodOptions = goodOptionRepository.findAll();
            // Then
            assertThat(goodOptions).isNotNull().hasSize(2);
        }

        @DisplayName("상품옵션 findById")
        @Test
        void getGoodOption() {
            // Given
            Long goodOptionId = 1L;
            // When
            Optional<GoodOption> goodOption = goodOptionRepository.findById(goodOptionId);
            // Then
            assertThat(goodOption).isNotNull();
        }

        @DisplayName("상품옵션 save")
        @Test
        void saveGoodOption() {
            // Given
            Article article = articleRepository.getReferenceById(1L);
            GoodOption goodOption = Fixture.getGoodOption(article);
            long previousCount = goodOptionRepository.count();
            log.info("previousCount: {}", previousCount);
            // When
            GoodOption savedGoodOption = goodOptionRepository.save(goodOption);
            // Then
            long afterCount = goodOptionRepository.count();
            log.info("afterCount: {}", afterCount);
            assertThat(afterCount).isEqualTo(previousCount + 1);
            assertThat(savedGoodOption)
                    .hasFieldOrPropertyWithValue("id", 3L);
        }

        @DisplayName("상품옵션 update")
        @Test
        void updateGoodOption() {
            // Given
            Long goodOptionId = 1L;
            int modified_price = 5000;
            GoodOption goodOption = goodOptionRepository.getReferenceById(goodOptionId);
            goodOption.setAddPrice(modified_price);
            LocalDateTime previousModifiedAt = goodOption.getModifiedAt();
            // When
            goodOptionRepository.saveAndFlush(goodOption);
            // Then
            assertThat(goodOption).hasFieldOrPropertyWithValue("addPrice", modified_price);
            assertThat(goodOption.getModifiedBy()).isEqualTo(goodOption.getCreatedBy());
            assertThat(goodOption.getModifiedAt()).isNotEqualTo(previousModifiedAt);

        }

        @DisplayName("상품옵션 delete")
        @Test
        void deleteGoodOption() {
            // Given
            Long goodOptionId = 1L;
            long previousCount = goodOptionRepository.count();
            log.info("previousCount: {}", previousCount);
            // When
            goodOptionRepository.deleteById(goodOptionId);
            // Then
            assertThat(goodOptionRepository.count()).isEqualTo(previousCount - 1);
        }
    }

    @ActiveProfiles("test")
    @DisplayName("치수 Jpa 테스트")
    @Import(TestJpaConfig.class)
    @DataJpaTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Nested
    public class DimensionJpaTest {

        @Autowired private DimensionRepository dimensionRepository;
        @Autowired private GoodOptionRepository goodOptionRepository;

        @DisplayName("치수 findAll")
        @Test
        void getDimensions() {
            // Given

            // When
            List<Dimension> dimensions = dimensionRepository.findAll();
            // Then
            assertThat(dimensions).isNotNull().hasSize(2);
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
        void saveDimension() {
            // Given
            GoodOption goodOption = goodOptionRepository.getReferenceById(1L);
            Dimension dimension = Fixture.getDimension(goodOption);
            long previousCount = dimensionRepository.count();
            log.info("previousCount: {}", previousCount);
            // When
            Dimension savedDimension = dimensionRepository.save(dimension);
            // Then
            long afterCount = dimensionRepository.count();
            log.info("afterCount: {}", afterCount);
            assertThat(afterCount).isEqualTo(previousCount + 1);
            assertThat(savedDimension)
                    .hasFieldOrPropertyWithValue("id", 3L);
        }

        @DisplayName("치수 update")
        @Test
        void updateDimension() {
            // Given
            Long dimensionId = 1L;
            DimUnit modified_unit = DimUnit.INCH;
            Dimension dimension = dimensionRepository.getReferenceById(dimensionId);
            dimension.setDimUnit(modified_unit);
            LocalDateTime previousModifiedAt = dimension.getModifiedAt();
            // When
            dimensionRepository.saveAndFlush(dimension);
            // Then
            assertThat(dimension).hasFieldOrPropertyWithValue("dimUnit", modified_unit);
            assertThat(dimension.getModifiedBy()).isEqualTo(dimension.getCreatedBy());
            assertThat(dimension.getModifiedAt()).isNotEqualTo(previousModifiedAt);

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
    @DisplayName("가격 Jpa 테스트")
    @Import(TestJpaConfig.class)
    @DataJpaTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Nested
    public class PriceJpaTest {

        @Autowired private PriceRepository priceRepository;

        @DisplayName("가격 findAll")
        @Test
        void getPrices() {
            // Given

            // When
            List<Price> prices = priceRepository.findAll();
            // Then
            assertThat(prices).isNotNull().hasSize(10);
        }

        @DisplayName("가격 findById")
        @Test
        void getPrice() {
            // Given
            Long priceId = 1L;
            // When
            Optional<Price> price = priceRepository.findById(priceId);
            // Then
            assertThat(price).isNotNull();
        }
    }

    @ActiveProfiles("test")
    @DisplayName("주문 Jpa 테스트")
    @Import(TestJpaConfig.class)
    @DataJpaTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Nested
    public class OrdersJpaTest {

        @Autowired private OrdersRepository ordersRepository;
        @Autowired private UserAccountRepository userAccountRepository;
        @Autowired private CompanyRepository companyRepository;

        @DisplayName("주문 findAll")
        @Test
        void getOrders() {
            // Given

            // When
            List<Orders> orders = ordersRepository.findAll();
            // Then
            assertThat(orders).isNotNull().hasSize(4);
        }

        @DisplayName("유저 이메일로 주문 findAll")
        @Test
        void getOrdersByEmail() {
            // Given
            String email = "a@gmail.com";
            // When
            List<Orders> orders = ordersRepository.findByUserAccount_Email(email);
            // Then
            assertThat(orders).isNotNull().hasSize(4);
        }

        @DisplayName("기업 id로 주문 findAll")
        @Test
        void getOrdersByCompanyId() {
            // Given
            Long companyId = 1L;
            // When
            List<Orders> orders = ordersRepository.findByCompanyId(companyId);
            // Then
            assertThat(orders).isNotNull().hasSize(4);
        }

        @DisplayName("주문 findById")
        @Test
        void getOrder() {
            // Given
            Long ordersId = 1L;
            // When
            Optional<Orders> order = ordersRepository.findById(ordersId);
            // Then
            assertThat(order).isNotNull();
        }

        @DisplayName("주문 save")
        @Test
        void saveOrder() {
            // Given
            Long userAccountId = 2L;
            UserAccount userAccount = userAccountRepository.getReferenceById(userAccountId);
            Company company = companyRepository.getReferenceById(1L);
            Orders orders = Fixture.getOrders(userAccount, company);
            long previousCount = ordersRepository.count();
            log.info("previousCount: {}", previousCount);
            // When
            Orders savedOrder = ordersRepository.save(orders);
            // Then
            long afterCount = ordersRepository.count();
            log.info("afterCount: {}", afterCount);
            assertThat(afterCount).isEqualTo(previousCount + 1);
            assertThat(savedOrder)
                    .hasFieldOrPropertyWithValue("id", 5L);
        }

        @DisplayName("주문 update")
        @Test
        void updateOrder() {
            // Given
            Long ordersId = 1L;
            OrderStatus modified_status = OrderStatus.DELIVERY;
            Orders orders = ordersRepository.getReferenceById(ordersId);
            orders.setStatus(modified_status);
            LocalDateTime previousModifiedAt = orders.getModifiedAt();
            // When
            ordersRepository.saveAndFlush(orders);
            // Then
            assertThat(orders).hasFieldOrPropertyWithValue("status", modified_status);
            assertThat(orders.getModifiedBy()).isNotEqualTo(orders.getCreatedBy());
            assertThat(orders.getModifiedAt()).isNotEqualTo(previousModifiedAt);

        }

        @DisplayName("주문 delete")
        @Test
        void deleteOrder() {
            // Given
            Long ordersId = 1L;
            long previousCount = ordersRepository.count();
            log.info("previousCount: {}", previousCount);
            // When
            ordersRepository.deleteById(ordersId);
            // Then
            assertThat(ordersRepository.count()).isEqualTo(previousCount - 1);
        }
    }

    @ActiveProfiles("test")
    @DisplayName("알람 Jpa 테스트")
    @Import(TestJpaConfig.class)
    @DataJpaTest
    @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
    @Nested
    public class AlarmJpaTest {

        @Autowired private AlarmRepository alarmRepository;
        @Autowired private UserAccountRepository userAccountRepository;

        @DisplayName("알람 findAll")
        @Test
        void getAlarms() {
            // Given

            // When
            List<Alarm> alarms = alarmRepository.findAll();
            // Then
            assertThat(alarms).isNotNull().hasSize(3);
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
            Long userAccountId = 3L;
            UserAccount userAccount = userAccountRepository.getReferenceById(userAccountId);
            Alarm alarm = Fixture.getAlarm(userAccount);
            long previousCount = alarmRepository.count();
            log.info("previousCount: {}", previousCount);
            // When
            Alarm savedAlarm = alarmRepository.save(alarm);
            // Then
            long afterCount = alarmRepository.count();
            log.info("afterCount: {}", afterCount);
            assertThat(afterCount).isEqualTo(previousCount + 1);
            assertThat(savedAlarm)
                    .hasFieldOrPropertyWithValue("id", 4L);
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
