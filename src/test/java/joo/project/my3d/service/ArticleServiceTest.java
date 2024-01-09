package joo.project.my3d.service;

import com.querydsl.core.types.Predicate;
import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.QArticle;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.dto.ArticleDto;
import joo.project.my3d.dto.ArticleFormDto;
import joo.project.my3d.dto.ArticleWithCommentsAndLikeCountDto;
import joo.project.my3d.dto.ArticlePreviewDto;
import joo.project.my3d.exception.ArticleException;
import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.repository.ArticleCommentRepository;
import joo.project.my3d.repository.ArticleLikeRepository;
import joo.project.my3d.repository.ArticleRepository;
import joo.project.my3d.repository.UserAccountRepository;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
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

    @DisplayName("1. 게시판에 표시할 전체 게시글 조회 (제목 검색)")
    @Test
    void getArticles_ForBoard() {
        // Given
        Pageable pageable = Pageable.ofSize(9);
        String title = "title";
        Predicate predicate = QArticle.article.title.eq(title);
        given(articleRepository.findAll(any(Predicate.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(Fixture.getArticle())));
        // When
        Page<ArticlePreviewDto> articles = articleService.getArticlesForPreview(predicate, pageable);
        // Then
        assertThat(articles.getTotalElements()).isEqualTo(1);
        assertThat(articles.getTotalPages()).isEqualTo(1);
        assertThat(articles.getContent().get(0).title()).isEqualTo(title);
        then(articleRepository).should().findAll(any(Predicate.class), any(Pageable.class));
    }

    @DisplayName("2. 추가/수정을 위한 게시글 조회")
    @Test
    void getArticles_ForUpdate() {
        // Given
        Article article = Fixture.getArticle();
        given(articleRepository.findByIdFetchForm(anyLong())).willReturn(Optional.of(article));
        // When
        ArticleFormDto articleFormDto = articleService.getArticleForm(1L);
        // Then
        assertThat(articleFormDto.title()).isEqualTo("title");
        assertThat(articleFormDto.content()).isEqualTo("content");
        assertThat(articleFormDto.articleType()).isEqualTo(ArticleType.MODEL);
        assertThat(articleFormDto.articleCategory()).isEqualTo(ArticleCategory.ARCHITECTURE);
        assertThat(articleFormDto.articleFileWithDimensionOptionWithDimensionDto().originalFileName()).isEqualTo("test.stp");
        then(articleRepository).should().findByIdFetchForm(anyLong());
    }

    @DisplayName("3. [예외 - 게시글 없음] 추가/수정을 위한 게시글 조회")
    @Test
    void getArticles_ForUpdate_Failed() {
        // Given
        given(articleRepository.findByIdFetchForm(anyLong()))
                .willThrow(new ArticleException(ErrorCode.ARTICLE_NOT_FOUND));
        // When
        assertThatThrownBy(() -> articleService.getArticleForm(1L))
                .isInstanceOf(ArticleException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ARTICLE_NOT_FOUND);
        // Then
        then(articleRepository).should().findByIdFetchForm(anyLong());
    }

    @DisplayName("4. 상세 정보를 포함한 게시글 조회 (댓글과 좋아요 개수를 포함)")
    @Test
    void getArticleDetail() {
        // Given
        Article article = Fixture.getArticle();
        article.setLikeCount(2);
        article.getArticleComments().add(Fixture.getArticleComment("comment"));
        given(articleRepository.findByIdFetchDetail(anyLong())).willReturn(Optional.of(article));
        // When
        ArticleWithCommentsAndLikeCountDto articleWithComments = articleService.getArticleWithComments(1L);
        // Then
        assertThat(articleWithComments.title()).isEqualTo("title");
        assertThat(articleWithComments.content()).isEqualTo("content");
        assertThat(articleWithComments.articleType()).isEqualTo(ArticleType.MODEL);
        assertThat(articleWithComments.articleCategory()).isEqualTo(ArticleCategory.ARCHITECTURE);
        assertThat(articleWithComments.articleFile().originalFileName()).isEqualTo("test.stp");
        assertThat(articleWithComments.likeCount()).isEqualTo(2);
        assertThat(articleWithComments.articleComments().size()).isEqualTo(1);
        then(articleRepository).should().findByIdFetchDetail(anyLong());
    }

    @DisplayName("5. [예외 - 게시글 없음] 상세 정보를 포함한 게시글 조회 (댓글과 좋아요 개수를 포함)")
    @Test
    void getArticles_ForDetail_Failed() {
        // Given
        given(articleRepository.findByIdFetchDetail(anyLong()))
                .willThrow(new ArticleException(ErrorCode.ARTICLE_NOT_FOUND));
        // When
        assertThatThrownBy(() -> articleService.getArticleWithComments(1L))
                .isInstanceOf(ArticleException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ARTICLE_NOT_FOUND);
        // Then
        then(articleRepository).should().findByIdFetchDetail(anyLong());
    }

    @DisplayName("6. 게시글 저장")
    @Test
    void saveArticle() {
        // Given
        ArticleDto articleDto = FixtureDto.getArticleDto(1L, "title",  "content", ArticleType.MODEL, ArticleCategory.ARCHITECTURE);
        Article article = Fixture.getArticle(articleDto.title(), articleDto.content(), articleDto.articleType(), articleDto.articleCategory());
        given(userAccountRepository.getReferenceByEmail(anyString())).willReturn(article.getUserAccount());
        given(articleRepository.save(any(Article.class))).willReturn(article);
        // When
        articleService.saveArticle(article.getUserAccount().getEmail(), articleDto);
        // Then
        then(userAccountRepository).should().getReferenceByEmail(anyString());
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("7. 게시글 수정")
    @Test
    void updateArticle() throws IllegalAccessException {
        // Given
        ArticleDto updatedArticle = FixtureDto.getArticleDto(1L, "new title", "new content", ArticleType.MODEL, ArticleCategory.ARCHITECTURE);
        Article savedArticle = Fixture.getArticle();
        FieldUtils.writeField(savedArticle, "id", 1L, true);
        given(articleRepository.findByIdAndUserAccount_Email(anyLong(), anyString()))
                .willReturn(Optional.of(savedArticle));
        // When
        articleService.updateArticle(1L, updatedArticle, updatedArticle.userAccountDto().email());
        // Then
        assertThat(savedArticle)
                .hasFieldOrPropertyWithValue("title", "new title")
                .hasFieldOrPropertyWithValue("content", "new content");
        then(articleRepository).should().findByIdAndUserAccount_Email(anyLong(), anyString());
    }

    @DisplayName("8. [예외 - 없는 게시글] 게시글 수정")
    @Test
    void updateArticle_NotExistArticle() {
        // Given
        ArticleDto updatedArticle = FixtureDto.getArticleDto(1L, "new title", "new content", ArticleType.MODEL, ArticleCategory.ARCHITECTURE);
        given(articleRepository.findByIdAndUserAccount_Email(anyLong(), anyString()))
                .willThrow(new ArticleException(ErrorCode.ARTICLE_NOT_FOUND));
        // When
        assertThatThrownBy(() -> articleService.updateArticle(1L, updatedArticle, updatedArticle.userAccountDto().email()))
                .isInstanceOf(ArticleException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ARTICLE_NOT_FOUND);
        // Then
        then(articleRepository).should().findByIdAndUserAccount_Email(anyLong(), anyString());
    }

    @DisplayName("9. [예외 - 작성자와 수정자가 상이] 게시글 수정")
    @Test
    void updateArticle_NotEqualWriter() throws IllegalAccessException {
        // Given
        ArticleDto updatedArticle = FixtureDto.getArticleDto(1L, "new title", "new content", ArticleType.MODEL, ArticleCategory.ARCHITECTURE);
        Article savedArticle = Fixture.getArticle();
        FieldUtils.writeField(savedArticle, "id", 1L, true);
        given(articleRepository.findByIdAndUserAccount_Email(anyLong(), anyString()))
                .willReturn(Optional.of(savedArticle));
        // When
        assertThatThrownBy(() -> articleService.updateArticle(1L, updatedArticle, "notwriter@gmail.com"))
                .isInstanceOf(ArticleException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_WRITER);
        // Then
        then(articleRepository).should().findByIdAndUserAccount_Email(anyLong(), anyString());
    }

    @DisplayName("10. 게시글 삭제")
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

    @DisplayName("11. [예외 - 게시글 없음]게시글 삭제")
    @Test
    void deleteArticle_NotExist() {
        // Given
        Long articleId = 1L;
        String email = "jk042386@gmail.com";
        given(articleRepository.getReferenceById(anyLong()))
                .willThrow(new ArticleException(ErrorCode.ARTICLE_NOT_FOUND));
        // When
        assertThatThrownBy(() -> articleService.deleteArticle(articleId, email))
                .isInstanceOf(ArticleException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ARTICLE_NOT_FOUND);
        // Then
        then(articleRepository).should().getReferenceById(anyLong());
    }

    @DisplayName("12. [예외 - 작성자와 요청자 상이]게시글 삭제")
    @Test
    void deleteArticle_NotEqualWriter() {
        // Given
        Article article = Fixture.getArticle();
        Long articleId = 1L;
        String email = "notwriter@gmail.com";
        given(articleRepository.getReferenceById(anyLong())).willReturn(article);
        // When
        assertThatThrownBy(() -> articleService.deleteArticle(articleId, email))
                .isInstanceOf(ArticleException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_WRITER);
        // Then
        then(articleRepository).should().getReferenceById(anyLong());
    }

    @DisplayName("13. [예외 - 삭제 실패]게시글 삭제")
    @Test
    void deleteArticle_FailedDelete() {
        // Given
        Article article = Fixture.getArticle();
        Long articleId = 1L;
        String email = "jk042386@gmail.com";
        given(articleRepository.getReferenceById(anyLong())).willReturn(article);
        willThrow(new ArticleException(ErrorCode.FAILED_DELETE))
                .given(articleCommentRepository).deleteByArticleId(articleId);
        // When
        assertThatThrownBy(() -> articleService.deleteArticle(articleId, email))
                .isInstanceOf(ArticleException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FAILED_DELETE);
        // Then
        then(articleRepository).should().getReferenceById(anyLong());
        then(articleCommentRepository).should().deleteByArticleId(articleId);
    }
}
