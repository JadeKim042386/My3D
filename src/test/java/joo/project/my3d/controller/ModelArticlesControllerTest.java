package joo.project.my3d.controller;

import com.querydsl.core.types.Predicate;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.dto.ArticleWithCommentsAndLikeCountDto;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.service.ArticleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러 - 게시글")
@WebMvcTest(ModelArticlesController.class)
class ModelArticlesControllerTest {
    @Autowired private MockMvc mvc;
    @MockBean private ArticleService articleService;

    @DisplayName("[GET] 게시판 페이지")
    @Test
    void modelArticles() throws Exception {
        // Given
        given(articleService.getArticles(any(Predicate.class), any(Pageable.class))).willReturn(Page.empty());
        // When
        mvc.perform(
                get("/model_articles")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("model_articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("modelPath"));

        // Then
        then(articleService).should().getArticles(any(Predicate.class), any(Pageable.class));
    }

    @DisplayName("[GET] 게시글 페이지")
    @Test
    void modelArticle() throws Exception {
        // Given
        Long articleId = 1L;
        ArticleWithCommentsAndLikeCountDto dto = FixtureDto.getArticleWithCommentsAndLikeCountDto("title", "content", ArticleType.MODEL, ArticleCategory.ARCHITECTURE);
        given(articleService.getArticle(articleId)).willReturn(dto);
        // When
        mvc.perform(
                        get("/model_articles/1")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("model_articles/detail"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("articleComments"))
                .andExpect(model().attributeExists("articleFile"));

        // Then
        then(articleService).should().getArticle(articleId);
    }
}
