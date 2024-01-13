package joo.project.my3d.controller;

import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.dto.ArticleCommentDto;
import joo.project.my3d.service.ArticleCommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@DisplayName("View 컨트롤러 - 댓글")
@Import(TestSecurityConfig.class)
@WebMvcTest(ArticleCommentsController.class)
class ArticleCommentsControllerTest {

    @Autowired private MockMvc mvc;
    @MockBean private ArticleCommentService articleCommentService;

    @DisplayName("[GET] 댓글 추가")
    @WithUserDetails(value = "jooUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void addComment() throws Exception {
        // Given
        willDoNothing().given(articleCommentService).saveComment(any(ArticleCommentDto.class));
        // When
        mvc.perform(
                post("/comments")
                        .param("content", "content")
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
        // Then
    }

    @DisplayName("[GET] 댓글 삭제")
    @WithUserDetails(value = "jooUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void deleteComment() throws Exception {
        // Given
        long articleCommentId = 1L;
        willDoNothing().given(articleCommentService).deleteComment(anyLong(), anyString());
        // When
        mvc.perform(
                        delete("/comments/" + articleCommentId)
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
        // Then
    }
}
