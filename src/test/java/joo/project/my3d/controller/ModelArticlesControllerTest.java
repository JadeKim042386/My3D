package joo.project.my3d.controller;

import com.querydsl.core.types.Predicate;
import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.dto.ArticleDto;
import joo.project.my3d.dto.ArticleWithCommentsAndLikeCountDto;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.service.ArticleService;
import joo.project.my3d.service.PaginationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러 - 게시글")
@Import(TestSecurityConfig.class)
@WebMvcTest(ModelArticlesController.class)
class ModelArticlesControllerTest {

    @Autowired private MockMvc mvc;
    @MockBean private ArticleService articleService;
    @MockBean private PaginationService paginationService;

    @DisplayName("[GET] 게시판 페이지")
    @WithMockUser
    @Test
    void modelArticles() throws Exception {
        // Given
        given(articleService.getArticles(any(Predicate.class), any(Pageable.class))).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of());
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
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @DisplayName("[GET] 게시글 작성 페이지")
    @WithUserDetails(value = "jooCompany", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void writeNewModelArticle() throws Exception {
        // Given

        // When
        mvc.perform(
                        get("/model_articles/form")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("model_articles/form"))
                .andExpect(model().attributeExists("formStatus"))
                .andExpect(model().attributeExists("categories"));

        // Then
    }

    @DisplayName("[POST] 게시글 추가")
    @WithUserDetails(value = "jooCompany", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void addNewModelArticle() throws Exception {
        // Given
        byte[] file = Fixture.getMultipartFile().getBytes();
        willDoNothing().given(articleService).saveArticle(any(ArticleDto.class));
        // When
        mvc.perform(
                        multipart("/model_articles/form")
                                .file("file", file)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "title")
                                .param("content", "content")
                                .param("articleCategory", "MUSIC")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/model_articles"))
                .andExpect(redirectedUrl("/model_articles"));

        // Then
        then(articleService).should().saveArticle(any(ArticleDto.class));
    }

    @DisplayName("[POST] 게시글 추가 - 파일 누락")
    @WithUserDetails(value = "jooCompany", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void addNewModelArticleWithoutFile() throws Exception {
        // Given

        // When
        mvc.perform(
                        multipart("/model_articles/form")
                                .file("file", null)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "title")
                                .param("content", "content")
                                .param("articleCategory", "MUSIC")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("model_articles/form"));

        // Then
    }

    @DisplayName("[POST] 게시글 추가 - 카테고리 누락")
    @WithUserDetails(value = "jooCompany", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void addNewModelArticleWithoutCategory() throws Exception {
        // Given
        byte[] file = Fixture.getMultipartFile().getBytes();
        // When
        mvc.perform(
                        multipart("/model_articles/form")
                                .file("file", file)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "title")
                                .param("content", "content")
                                .param("articleCategory", "카테고리를 선택해주세요.")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("model_articles/form"));

        // Then
    }

    @DisplayName("[POST] 게시글 추가 - 제목 누락")
    @WithUserDetails(value = "jooCompany", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void addNewModelArticleWithoutTitle() throws Exception {
        // Given
        byte[] file = Fixture.getMultipartFile().getBytes();
        // When
        mvc.perform(
                        multipart("/model_articles/form")
                                .file("file", file)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "")
                                .param("content", "content")
                                .param("articleCategory", "MUSIC")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("model_articles/form"));

        // Then
    }

    @DisplayName("[POST] 게시글 추가 - 본문 누락")
    @WithUserDetails(value = "jooCompany", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void addNewModelArticleWithoutContent() throws Exception {
        // Given
        byte[] file = Fixture.getMultipartFile().getBytes();
        // When
        mvc.perform(
                        multipart("/model_articles/form")
                                .file("file", file)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "title")
                                .param("content", "")
                                .param("articleCategory", "MUSIC")
                )
                .andExpect(status().isOk())
                .andExpect(view().name("model_articles/form"));

        // Then
    }

    @DisplayName("[GET] 게시글 페이지")
    @WithMockUser
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
