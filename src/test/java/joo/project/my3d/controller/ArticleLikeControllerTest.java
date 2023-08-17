package joo.project.my3d.controller;

import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.service.ArticleLikeService;
import joo.project.my3d.service.ArticleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러 - 게시글 좋아요")
@Import(TestSecurityConfig.class)
@WebMvcTest(ArticleLikeController.class)
class ArticleLikeControllerTest {

    @Autowired private MockMvc mvc;
    @MockBean private ArticleLikeService articleLikeService;

    @DisplayName("[GET] 게시글 좋아요 추가")
    @WithUserDetails(value = "jooUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void addArticleLike() throws Exception {
        // Given
        Long articleId = 1L;
        willDoNothing().given(articleLikeService).addArticleLike(anyLong(), anyString());
        // When
        mvc.perform(
                get("/like/" + articleId)
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/model_articles/" + articleId))
                .andExpect(redirectedUrl("/model_articles/" + articleId));
        // Then
        then(articleLikeService).should().addArticleLike(anyLong(), anyString());
    }

    @DisplayName("[GET] 게시글 좋아요 삭제")
    @WithMockUser
    @Test
    void deleteArticleLike() throws Exception {
        // Given
        Long articleId = 1L;
        willDoNothing().given(articleLikeService).deleteArticleLike(anyLong());
        // When
        mvc.perform(
                        get("/like/" + articleId + "/delete")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/model_articles/" + articleId))
                .andExpect(redirectedUrl("/model_articles/" + articleId));
        // Then
        then(articleLikeService).should().deleteArticleLike(anyLong());
    }
}