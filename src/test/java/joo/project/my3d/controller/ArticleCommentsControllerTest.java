package joo.project.my3d.controller;

import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.dto.ArticleCommentDto;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.service.ArticleCommentService;
import lombok.With;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러 - 댓글")
@Import(TestSecurityConfig.class)
@WebMvcTest(ArticleCommentsController.class)
class ArticleCommentsControllerTest {

    @Autowired private MockMvc mvc;
    @MockBean private ArticleCommentService articleCommentService;

    @DisplayName("[GET] 댓글 추가")
    @WithUserDetails(value = "jooUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void addComment() throws Exception {
        // Given
        willDoNothing().given(articleCommentService).saveComment(any(ArticleCommentDto.class));
        // When
        mvc.perform(
                post("/comments/new")
                        .queryParam("articleId", "1")
        )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/model_articles/1"))
                .andExpect(redirectedUrl("/model_articles/1"));
        // Then
        then(articleCommentService).should().saveComment(any(ArticleCommentDto.class));
    }

    @DisplayName("[GET] 댓글 삭제")
    @WithUserDetails(value = "jooUser", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void deleteComment() throws Exception {
        // Given
        Long articleCommentId = 1L;
        Long articleId = 1L;
        willDoNothing().given(articleCommentService).deleteComment(anyLong(), anyString());
        // When
        mvc.perform(
                        post("/comments/" + articleCommentId + "/delete")
                                .queryParam("articleId", String.valueOf(articleId))
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/model_articles/" + articleId))
                .andExpect(redirectedUrl("/model_articles/" + articleId));
        // Then
        then(articleCommentService).should().deleteComment(anyLong(), anyString());
    }
}
