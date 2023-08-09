package joo.project.my3d.service;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.dto.ArticleDto;
import joo.project.my3d.dto.ArticleWithCommentsAndLikeCountDto;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("비지니스 로직 - 모델 게시글")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {
    @InjectMocks private ArticleService articleService;
    @Mock private ArticleRepository articleRepository;

    @DisplayName("게시글 페이지 반환")
    @Test
    void getArticles() {
        // Given
        Pageable pageable = Pageable.ofSize(9);
        given(articleRepository.findAll(pageable)).willReturn(Page.empty());
        // When
        List<Article> articles = articleService.getArticles(pageable);
        // Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(pageable);
    }

    @DisplayName("단일 게시글 조회")
    @Test
    void getArticle() {
        // Given
        Long articleId = 1L;
        Article article = Fixture.getArticle("title", "content", ArticleType.MODEL);
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));
        // When
        ArticleWithCommentsAndLikeCountDto dto = articleService.getArticle(articleId);
        // Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("articleType", article.getArticleType());
        then(articleRepository).should().findById(articleId);
    }

    @DisplayName("게시글 저장")
    @Test
    void saveArticle() {
        // Given
        ArticleDto articleDto = FixtureDto.getArticleDto(1L, "title", "content", ArticleType.MODEL, ArticleCategory.ARCHITECTURE);
        Article article = Fixture.getArticle("title", "content", ArticleType.MODEL);
        given(articleRepository.save(any(Article.class))).willReturn(article);
        // When
        articleService.saveArticle(articleDto);
        // Then
        then(articleRepository).should().save(any(Article.class));
    }

    @DisplayName("게시글 수정")
    @Test
    void updateArticle() {
        // Given
        ArticleDto articleDto = FixtureDto.getArticleDto(1L, "title", "content", ArticleType.MODEL, ArticleCategory.ARCHITECTURE);
        Article article = Fixture.getArticle("new title", "new content", ArticleType.MODEL);
        given(articleRepository.getReferenceById(articleDto.id())).willReturn(article);
        // When
        articleService.updateArticle(articleDto);
        // Then
        assertThat(article)
                .hasFieldOrPropertyWithValue("title", articleDto.title())
                .hasFieldOrPropertyWithValue("content", articleDto.content());
        then(articleRepository).should().getReferenceById(articleDto.id());
    }

    @DisplayName("[예외-없는 게시글] 게시글 수정")
    @Test
    void updateArticleNotExistArticle() {
        // Given
        ArticleDto articleDto = FixtureDto.getArticleDto(1L, "title", "content", ArticleType.MODEL, ArticleCategory.ARCHITECTURE);
        given(articleRepository.getReferenceById(articleDto.id())).willThrow(EntityNotFoundException.class);
        // When
        articleService.updateArticle(articleDto);
        // Then
        then(articleRepository).should().getReferenceById(articleDto.id());
    }

    @DisplayName("게시글 삭제")
    @Test
    void deleteArticle() {
        // Given
        Long articleId = 1L;
        willDoNothing().given(articleRepository).deleteById(articleId);
        // When
        articleService.deleteArticle(articleId);
        // Then
        then(articleRepository).should().deleteById(articleId);
    }
}
