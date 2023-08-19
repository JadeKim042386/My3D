package joo.project.my3d.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.PredicateOperation;
import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.QArticle;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.ArticleDto;
import joo.project.my3d.dto.ArticleWithCommentsAndLikeCountDto;
import joo.project.my3d.exception.ArticleException;
import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.exception.FileException;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.repository.ArticleCommentRepository;
import joo.project.my3d.repository.ArticleLikeRepository;
import joo.project.my3d.repository.ArticleRepository;
import joo.project.my3d.repository.UserAccountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@DisplayName("비지니스 로직 - 모델 게시글")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {
    @InjectMocks private ArticleService articleService;
    @Mock private ArticleRepository articleRepository;
    @Mock private ArticleCommentRepository articleCommentRepository;
    @Mock private ArticleLikeRepository articleLikeRepository;
    @Mock private UserAccountRepository userAccountRepository;

    @DisplayName("게시글 페이지 반환")
    @Test
    void getArticles() {
        // Given
        Pageable pageable = Pageable.ofSize(9);
        Predicate predicate = new BooleanBuilder();
        given(articleRepository.findAll(predicate, pageable)).willReturn(Page.empty());
        // When
        Page<ArticleDto> articles = articleService.getArticles(predicate, pageable);
        // Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(predicate, pageable);
    }

    @DisplayName("카테고리로 게시글 검색")
    @Test
    void getArticleByArticleCategory() {
        // Given
        Pageable pageable = Pageable.ofSize(9);
        ArticleCategory articleCategory = ArticleCategory.MUSIC;
        Predicate predicate = QArticle.article.articleCategory.eq(articleCategory);
        given(articleRepository.findAll(predicate, pageable)).willReturn(Page.empty());
        // When
        Page<ArticleDto> articles = articleService.getArticles(predicate, pageable);
        // Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(predicate, pageable);
    }

    @DisplayName("제목으로 게시글 검색")
    @Test
    void getArticleByTitle() {
        // Given
        Pageable pageable = Pageable.ofSize(9);
        String title = "title";
        Predicate predicate = QArticle.article.title.eq(title);
        given(articleRepository.findAll(predicate, pageable)).willReturn(Page.empty());
        // When
        Page<ArticleDto> articles = articleService.getArticles(predicate, pageable);
        // Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(predicate, pageable);
    }

    @DisplayName("카테고리+제목으로 게시글 검색")
    @Test
    void getArticleByArticleCategoryAndTitle() {
        // Given
        Pageable pageable = Pageable.ofSize(9);
        ArticleCategory articleCategory = ArticleCategory.MUSIC;
        String title = "title";
        Predicate predicate = QArticle.article.articleCategory.eq(articleCategory)
                .and(QArticle.article.title.eq(title));
        given(articleRepository.findAll(predicate, pageable)).willReturn(Page.empty());
        // When
        Page<ArticleDto> articles = articleService.getArticles(predicate, pageable);
        // Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(predicate, pageable);
    }

    @DisplayName("단일 게시글 조회")
    @Test
    void getArticle() {
        // Given
        Long articleId = 1L;
        Article article = Fixture.getArticle("title", "content", ArticleType.MODEL, ArticleCategory.ARCHITECTURE);
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));
        // When
        ArticleDto dto = articleService.getArticle(articleId);
        // Then
        assertThat(dto)
                .hasFieldOrProperty("userAccountDto")
                .hasFieldOrProperty("articleFileDto")
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("articleType", article.getArticleType())
                .hasFieldOrProperty("articleCategory")
                .hasFieldOrProperty("likeCount");
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("단일 게시글 조회 - 댓글포함")
    @Test
    void getArticleWithComments() {
        // Given
        Long articleId = 1L;
        Article article = Fixture.getArticle("title", "content", ArticleType.MODEL, ArticleCategory.ARCHITECTURE);
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));
        // When
        ArticleWithCommentsAndLikeCountDto dto = articleService.getArticleWithComments(articleId);
        // Then
        assertThat(dto)
                .hasFieldOrProperty("userAccountDto")
                .hasFieldOrProperty("articleFileDto")
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("articleType", article.getArticleType())
                .hasFieldOrProperty("articleCategory")
                .hasFieldOrProperty("likeCount")
                .hasFieldOrProperty("articleCommentDtos");
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("[예외-없는 게시글] 단일 게시글 조회")
    @Test
    void getArticleNotExistArticle() {
        // Given
        Long articleId = 1L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());
        // When
        assertThatThrownBy(() -> articleService.getArticle(articleId))
                .isInstanceOf(ArticleException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ARTICLE_NOT_FOUND);
        // Then
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글 저장")
    @Test
    void saveArticle() {
        // Given
        ArticleDto articleDto = FixtureDto.getArticleDto(1L, "title", "content", ArticleType.MODEL, ArticleCategory.ARCHITECTURE);
        Article article = Fixture.getArticle(articleDto.title(), articleDto.content(), articleDto.articleType(), articleDto.articleCategory());
        given(articleRepository.save(any(Article.class))).willReturn(article);
        // When
        articleService.saveArticle(articleDto);
        // Then
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("[예외-모델 게시글에 카테고리가 없음] 게시글 저장")
    @Test
    void saveModelArticleNotExistCategory() {
        // Given
        ArticleDto articleDto = FixtureDto.getArticleDto(1L, "title", "content", ArticleType.MODEL, null);
        // When
        assertThatThrownBy(() -> articleService.saveArticle(articleDto))
                .isInstanceOf(ArticleException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ARTICLE_CATEGORY_NOT_FOUND);
        // Then
        then(articleRepository).shouldHaveNoInteractions();
    }

    @DisplayName("게시글 수정")
    @Test
    void updateArticle() {
        // Given
        ArticleDto articleDto = FixtureDto.getArticleDto(1L, "new title", "new content", ArticleType.MODEL, ArticleCategory.ARCHITECTURE);
        Article article = Fixture.getArticle("title", "content", ArticleType.MODEL, ArticleCategory.ARCHITECTURE);
        UserAccount userAccount = Fixture.getUserAccount();
        given(articleRepository.getReferenceById(articleDto.id())).willReturn(article);
        given(userAccountRepository.getReferenceById(articleDto.userAccountDto().email())).willReturn(userAccount);
        // When
        articleService.updateArticle(1L, articleDto);
        // Then
        assertThat(article)
                .hasFieldOrPropertyWithValue("title", articleDto.title())
                .hasFieldOrPropertyWithValue("content", articleDto.content());
        then(articleRepository).should().getReferenceById(articleDto.id());
        then(userAccountRepository).should().getReferenceById(articleDto.userAccountDto().email());
    }

    @DisplayName("[예외-없는 게시글] 게시글 수정")
    @Test
    void updateArticleNotExistArticle() {
        // Given
        ArticleDto articleDto = FixtureDto.getArticleDto(1L, "title", "content", ArticleType.MODEL, ArticleCategory.ARCHITECTURE);
        given(articleRepository.getReferenceById(articleDto.id())).willThrow(EntityNotFoundException.class);
        // When
        assertThatThrownBy(() -> articleService.updateArticle(1L, articleDto))
                .isInstanceOf(ArticleException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ARTICLE_NOT_FOUND);
        // Then
        then(articleRepository).should().getReferenceById(articleDto.id());
    }

    @DisplayName("[예외-작성자와 수정자가 상이] 게시글 수정")
    @Test
    void updateArticleNotWriter() {
        // Given
        ArticleDto articleDto = FixtureDto.getArticleDto(1L, "new title", "new content", ArticleType.MODEL, ArticleCategory.ARCHITECTURE);
        Article article = Fixture.getArticle("title", "content", ArticleType.MODEL, ArticleCategory.ARCHITECTURE);
        UserAccount wrongUserAccount = Fixture.getUserAccount("a@gmail.com", "pw", "A", true, UserRole.COMPANY);
        given(articleRepository.getReferenceById(articleDto.id())).willReturn(article);
        given(userAccountRepository.getReferenceById(articleDto.userAccountDto().email())).willReturn(wrongUserAccount);
        // When
        assertThatThrownBy(() -> articleService.updateArticle(1L, articleDto))
                .isInstanceOf(ArticleException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ARTICLE_NOT_WRITER);
        // Then
        then(articleRepository).should().getReferenceById(articleDto.id());
        then(userAccountRepository).should().getReferenceById(articleDto.userAccountDto().email());
    }

    @DisplayName("게시글 삭제")
    @Test
    void deleteArticle() {
        // Given
        Article article = Fixture.getArticle();
        Long articleId = 1L;
        String email = "jk042386@gmail.com";
        given(articleRepository.getReferenceById(anyLong())).willReturn(article);
        willDoNothing().given(articleCommentRepository).deleteByArticleId(articleId);
        willDoNothing().given(articleLikeRepository).deleteByArticleId(articleId);
        willDoNothing().given(articleRepository).delete(article);
        // When
        articleService.deleteArticle(articleId, email);
        // Then
        then(articleRepository).should().getReferenceById(anyLong());
        then(articleCommentRepository).should().deleteByArticleId(articleId);
        then(articleLikeRepository).should().deleteByArticleId(articleId);
        then(articleRepository).should().delete(article);
    }

    @DisplayName("게시글 모델 파일 반환")
    @Test
    void getArticleFile() {
        // Given
        Long articleId = 1L;
        Article article = Fixture.getArticle("title", "content", ArticleType.MODEL, ArticleCategory.ARCHITECTURE);
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));
        // When
        articleService.getArticleFile(articleId);
        // Then
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("[예외-없는 파일] 게시글 모델 파일 반환")
    @Test
    void getArticleFileNotExist() {
        // Given
        Long articleId = 1L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());
        // When
        assertThatThrownBy(() -> articleService.getArticleFile(articleId))
                .isInstanceOf(FileException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_NOT_FOUND);
        // Then
        then(articleRepository).should().findById(articleId);
    }
}
