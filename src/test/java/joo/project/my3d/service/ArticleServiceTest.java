package joo.project.my3d.service;

import com.querydsl.core.types.Predicate;
import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.ArticleComment;
import joo.project.my3d.domain.QArticle;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.dto.ArticleFormDto;
import joo.project.my3d.dto.ArticlePreviewDto;
import joo.project.my3d.dto.response.ArticleDetailResponse;
import joo.project.my3d.exception.ArticleException;
import joo.project.my3d.exception.FileException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.repository.ArticleRepository;
import joo.project.my3d.repository.UserAccountRepository;
import joo.project.my3d.service.impl.ArticleFileService;
import joo.project.my3d.service.impl.ArticleLikeService;
import joo.project.my3d.service.impl.ArticleService;
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

import java.time.LocalDateTime;
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
    @InjectMocks
    private ArticleService articleService;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Mock
    private ArticleFileService articleFileService;

    @Mock
    private ArticleLikeService articleLikeService;

    @Mock
    private FileServiceInterface fileService;

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
        assertThat(articleFormDto.articleFileWithDimensionDto().originalFileName())
                .isEqualTo("test.stp");
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
    void getArticleDetail() throws IllegalAccessException {
        // Given
        Article article = Fixture.getArticle();
        FieldUtils.writeField(article, "createdAt", LocalDateTime.now(), true);
        ArticleComment comment = Fixture.getArticleComment("comment");
        FieldUtils.writeField(comment, "id", 1L, true);
        article.getArticleComments().add(comment);
        given(articleRepository.findByIdFetchDetail(anyLong())).willReturn(Optional.of(article));
        given(articleLikeService.getLikeCountByArticleId(anyLong())).willReturn(2);
        given(articleLikeService.addedLike(anyLong(), anyLong())).willReturn(true);
        // When
        ArticleDetailResponse articleDetailResponse = articleService.getArticleWithComments(
                1L, article.getUserAccount().getId());
        // Then
        assertThat(articleDetailResponse.likeCount()).isEqualTo(2);
        assertThat(articleDetailResponse.addedLike()).isEqualTo(true);
        assertThat(articleDetailResponse.article().title()).isEqualTo("title");
        assertThat(articleDetailResponse.article().content()).isEqualTo("content");
        assertThat(articleDetailResponse.article().articleType()).isEqualTo(ArticleType.MODEL);
        assertThat(articleDetailResponse.article().articleCategory()).isEqualTo(ArticleCategory.ARCHITECTURE);
        assertThat(articleDetailResponse.article().articleFile().originalFileName())
                .isEqualTo("test.stp");
        assertThat(articleDetailResponse.article().articleComments().size()).isEqualTo(1);
    }

    @DisplayName("5. [예외 - 게시글 없음] 상세 정보를 포함한 게시글 조회 (댓글과 좋아요 개수를 포함)")
    @Test
    void getArticles_ForDetail_Failed() {
        // Given
        given(articleRepository.findByIdFetchDetail(anyLong()))
                .willThrow(new ArticleException(ErrorCode.ARTICLE_NOT_FOUND));
        // When
        assertThatThrownBy(() -> articleService.getArticleWithComments(1L, 1L))
                .isInstanceOf(ArticleException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ARTICLE_NOT_FOUND);
        // Then
        then(articleRepository).should().findByIdFetchDetail(anyLong());
    }

    @DisplayName("6. 게시글 저장")
    @Test
    void saveArticle() throws IllegalAccessException {
        // Given
        ArticleFormDto articleDto =
                FixtureDto.getArticleFormDto(1L, "title", "content", ArticleType.MODEL, ArticleCategory.ARCHITECTURE);
        Article article = Fixture.getArticle(
                articleDto.title(), articleDto.content(), articleDto.articleType(), articleDto.articleCategory());
        given(userAccountRepository.getReferenceById(anyLong())).willReturn(article.getUserAccount());
        given(articleRepository.save(any(Article.class))).willReturn(article);
        willDoNothing().given(fileService).uploadFile(any(), anyString());
        // When
        articleService.saveArticle(article.getUserAccount().getId(), Fixture.getArticleFormRequest());
        // Then
        then(userAccountRepository).should().getReferenceById(anyLong());
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("7. 게시글 수정")
    @Test
    void updateArticle() throws IllegalAccessException {
        // Given
        Article savedArticle = Fixture.getArticle();
        FieldUtils.writeField(savedArticle, "id", 1L, true);
        given(articleRepository.existsByIdAndUserAccountId(anyLong(), anyLong())).willReturn(true);
        given(articleRepository.findByIdAndUserAccountId(anyLong(), anyLong()))
                .willReturn(Optional.of(savedArticle));
        willDoNothing().given(articleFileService).updateArticleFile(any(), any());
        // When
        articleService.updateArticle(Fixture.getArticleFormRequest(), 1L, 1L);
        // Then
        assertThat(savedArticle)
                .hasFieldOrPropertyWithValue("title", "new title")
                .hasFieldOrPropertyWithValue("content", "new content");
    }

    @DisplayName("8. [예외 - 없는 게시글] 게시글 수정")
    @Test
    void updateArticle_NotExistArticle() {
        // Given
        given(articleRepository.findByIdAndUserAccountId(anyLong(), anyLong()))
                .willThrow(new ArticleException(ErrorCode.ARTICLE_NOT_FOUND));
        // When
        assertThatThrownBy(() -> articleService.updateArticle(Fixture.getArticleFormRequest(), 1L, 1L))
                .isInstanceOf(ArticleException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ARTICLE_NOT_FOUND);
        // Then
    }

    @DisplayName("9. [예외 - 작성자와 수정자가 상이] 게시글 수정")
    @Test
    void updateArticle_NotEqualWriter() throws IllegalAccessException {
        // Given
        Article savedArticle = Fixture.getArticle();
        FieldUtils.writeField(savedArticle, "id", 1L, true);
        given(articleRepository.findByIdAndUserAccountId(anyLong(), anyLong()))
                .willReturn(Optional.of(savedArticle));
        // When
        assertThatThrownBy(
                        () -> articleService.updateArticle(Fixture.getArticleFormRequest(), 1L, 2L))
                .isInstanceOf(ArticleException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_WRITER);
        // Then
    }

    @DisplayName("10. 게시글 삭제")
    @Test
    void deleteArticle() {
        // Given
        Article article = Fixture.getArticle();
        Long articleId = 1L;
        Long userAccountId = 1L;
        given(articleRepository.existsByIdAndUserAccountId(anyLong(), anyLong())).willReturn(true);
        given(articleRepository.getReferenceById(anyLong())).willReturn(article);
        willDoNothing().given(articleFileService).deleteFile(anyLong());
        willDoNothing().given(articleRepository).delete(any());
        // When
        articleService.deleteArticle(articleId, userAccountId);
        // Then
    }

    @DisplayName("11. [예외 - 게시글 없음]게시글 삭제")
    @Test
    void deleteArticle_NotExist() {
        // Given
        Long articleId = 1L;
        Long userAccountId = 1L;
        given(articleRepository.existsByIdAndUserAccountId(anyLong(), anyLong())).willReturn(true);
        given(articleRepository.getReferenceById(anyLong()))
                .willThrow(new ArticleException(ErrorCode.ARTICLE_NOT_FOUND));
        // When
        assertThatThrownBy(() -> articleService.deleteArticle(articleId, userAccountId))
                .isInstanceOf(ArticleException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ARTICLE_NOT_FOUND);
        // Then
        then(articleRepository).should().getReferenceById(anyLong());
    }

    @DisplayName("12. [예외 - 작성자와 요청자 상이]게시글 삭제")
    @Test
    void deleteArticle_NotEqualWriter() {
        // Given
        Long articleId = 1L;
        Long userAccountId = 1L;
        given(articleRepository.existsByIdAndUserAccountId(anyLong(), anyLong())).willReturn(false);
        // When
        assertThatThrownBy(() -> articleService.deleteArticle(articleId, userAccountId))
                .isInstanceOf(ArticleException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_WRITER);
        // Then
    }

    @DisplayName("13. [예외 - 삭제 실패]게시글 삭제")
    @Test
    void deleteArticle_FailedDelete() {
        // Given
        Article article = Fixture.getArticle();
        Long articleId = 1L;
        Long userAccountId = 1L;
        given(articleRepository.existsByIdAndUserAccountId(anyLong(), anyLong())).willReturn(true);
        given(articleRepository.getReferenceById(anyLong())).willReturn(article);
        willThrow(new FileException(ErrorCode.FAILED_DELETE))
                .given(articleFileService)
                .deleteFile(anyLong());
        // When
        assertThatThrownBy(() -> articleService.deleteArticle(articleId, userAccountId))
                .isInstanceOf(FileException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FAILED_DELETE);
        // Then
    }
}
