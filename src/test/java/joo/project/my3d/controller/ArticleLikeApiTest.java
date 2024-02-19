package joo.project.my3d.controller;

import joo.project.my3d.api.ArticleLikeApi;
import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.fixture.FixtureCookie;
import joo.project.my3d.service.impl.ArticleLikeService;
import joo.project.my3d.service.impl.ArticleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@DisplayName("View 컨트롤러 - 게시글 좋아요")
@Import(TestSecurityConfig.class)
@WebMvcTest(ArticleLikeApi.class)
class ArticleLikeApiTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ArticleService articleService;

    @MockBean
    private ArticleLikeService articleLikeService;

    private static Cookie userCookie = FixtureCookie.createUserCookie();

    @DisplayName("[POST] 게시글 좋아요 추가")
    @Test
    void addArticleLike() throws Exception {
        // Given
        Long articleId = 1L;
        given(articleService.isExistsByArticleIdAndUserAccountId(anyLong(), anyLong())).willReturn(false);
        given(articleLikeService.addedLike(anyLong(), anyLong())).willReturn(false);
        given(articleLikeService.addArticleLike(anyLong(), anyLong())).willReturn(0);
        // When
        mvc.perform(post("/api/v1/articles/" + articleId + "/like")
                        .cookie(userCookie)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.likeCount").value(0));

        // Then
    }

    @DisplayName("[DELETE] 게시글 좋아요 삭제")
    @Test
    void deleteArticleLike() throws Exception {
        // Given
        Long articleId = 1L;
        given(articleService.isExistsByArticleIdAndUserAccountId(anyLong(), anyLong())).willReturn(false);
        given(articleLikeService.addedLike(anyLong(), anyLong())).willReturn(true);
        given(articleLikeService.deleteArticleLike(anyLong(), anyLong())).willReturn(1);
        // When
        mvc.perform(delete("/api/v1/articles/" + articleId + "/like")
                        .cookie(userCookie)
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.likeCount").value(1));
        // Then
    }
}
