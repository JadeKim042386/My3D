package joo.project.my3d.controller;

import joo.project.my3d.api.ArticleCommentsApi;
import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureCookie;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.service.impl.ArticleCommentService;
import joo.project.my3d.service.impl.UserAccountService;
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

import javax.servlet.http.Cookie;

import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@DisplayName("View 컨트롤러 - 댓글")
@Import(TestSecurityConfig.class)
@WebMvcTest(ArticleCommentsApi.class)
class ArticleCommentsApiTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ArticleCommentService articleCommentService;

    @Autowired
    private UserAccountService userAccountService;

    private static String apiUrl = "/api/v1/articles/1/comments";
    private static Cookie userCookie = FixtureCookie.createUserCookie();

    @DisplayName("[POST] 댓글 추가")
    @Test
    void addComment() throws Exception {
        // Given
        given(userAccountService.searchUserEntity(anyString())).willReturn(Fixture.getUserAccount());
        given(userAccountService.searchUserEntityByArticleId(anyLong())).willReturn(Fixture.getUserAccount());
        given(articleCommentService.saveComment(any(), any(), any()))
                .willReturn(FixtureDto.getArticleCommentDto("content"));
        // When
        mvc.perform(post(apiUrl).param("content", "content").cookie(userCookie).with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("content"));
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
        mvc.perform(delete(apiUrl + "/" + articleCommentId).cookie(userCookie).with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$").isNotEmpty());
        // Then
    }
}
