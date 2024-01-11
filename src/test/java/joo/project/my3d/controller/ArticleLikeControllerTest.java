package joo.project.my3d.controller;

import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.service.ArticleLikeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@DisplayName("View 컨트롤러 - 게시글 좋아요")
@Import(TestSecurityConfig.class)
@WebMvcTest(ArticleLikeController.class)
class ArticleLikeControllerTest {

    @Autowired private MockMvc mvc;
    @MockBean private ArticleLikeService articleLikeService;

    @DisplayName("[POST] 게시글 좋아요 추가")
    @Test
    void addArticleLike() throws Exception {
        // Given
        Long articleId = 1L;
        given(articleLikeService.addArticleLike(anyLong(), anyString())).willReturn(0);
        // When
        mvc.perform(
                post("/like/" + articleId)
                        .cookie(Fixture.getCookie())
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").value(0));

        // Then
    }

    @DisplayName("[DELETE] 게시글 좋아요 삭제")
    @WithMockUser
    @Test
    void deleteArticleLike() throws Exception {
        // Given
        Long articleId = 1L;
        given(articleLikeService.deleteArticleLike(anyLong())).willReturn(1);
        // When
        mvc.perform(
                delete("/like/" + articleId)
                        .cookie(Fixture.getCookie())
                        .with(csrf())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data").value(1));
        // Then
        then(articleLikeService).should().deleteArticleLike(anyLong());
    }
}
