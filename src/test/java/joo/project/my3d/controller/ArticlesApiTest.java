package joo.project.my3d.controller;

import joo.project.my3d.aop.BindingResultHandlerAspect;
import joo.project.my3d.api.ArticlesApi;
import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.domain.Article;
import joo.project.my3d.exception.ArticleException;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureCookie;
import joo.project.my3d.service.impl.ArticleFileService;
import joo.project.my3d.service.impl.ArticleService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;

import static joo.project.my3d.exception.constant.ErrorCode.ARTICLE_NOT_FOUND;
import static joo.project.my3d.exception.constant.ErrorCode.FAILED_DELETE;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@DisplayName("View 컨트롤러 - 게시글")
@Import({TestSecurityConfig.class, AopAutoConfiguration.class, BindingResultHandlerAspect.class})
@WebMvcTest(ArticlesApi.class)
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class ArticlesApiTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ArticleService articleService;

    @MockBean
    private ArticleFileService articleFileService;

    private static Cookie userCookie = FixtureCookie.createUserCookie();

    @Order(0)
    @DisplayName("[POST] 게시글 추가 - 정상")
    @Test
    void addNewModelArticle() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        Article article = Fixture.getArticle();
        given(articleService.saveArticle(anyLong(), any())).willReturn(article);
        // When
        mvc.perform(multipart(HttpMethod.POST, "/api/v1/articles")
                        .file(multipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("title", "title")
                        .param("content", "content")
                        .param("articleCategory", "MUSIC")
                        .param("dimensionOptions[0].optionName", "option1")
                        .param("dimensionOptions[0].dimensions[0].dimName", "dimName")
                        .param("dimensionOptions[0].dimensions[0].dimValue", String.valueOf(100.0))
                        .param("dimensionOptions[0].dimensions[0].dimUnit", "MM")
                        .cookie(userCookie)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty());

        // Then
    }

    @Order(1)
    @DisplayName("[POST][ValidationError] 게시글 추가(비어있는 파일) - 실패")
    @Test
    void addNewModelArticle_WithoutFile() throws Exception {
        // Given
        // When
        mvc.perform(multipart(HttpMethod.POST, "/api/v1/articles")
                        .file("modelFile", new byte[] {})
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("title", "title")
                        .param("content", "content")
                        .param("articleCategory", "MUSIC")
                        .param("dimensionOptions[0].optionName", "option1")
                        .param("dimensionOptions[0].printingTech", "123")
                        .param("dimensionOptions[0].material", "123")
                        .param("dimensionOptions[0].dimensions[0].dimName", "dimName")
                        .param("dimensionOptions[0].dimensions[0].dimValue", String.valueOf(100.0))
                        .param("dimensionOptions[0].dimensions[0].dimUnit", "MM")
                        .cookie(userCookie)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.errors.size()").value(1));
        // Then
    }

    @Order(2)
    @DisplayName("[POST][ValidationError] 게시글 추가(카테고리를 선택하지 않았을 경우) - 실패")
    @Test
    void addNewModelArticle_WithoutCategory() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        // When
        mvc.perform(multipart(HttpMethod.POST, "/api/v1/articles")
                        .file(multipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("title", "title")
                        .param("content", "content")
                        .param("articleCategory", "카테고리를 선택해주세요.")
                        .param("dimensionOptions[0].optionName", "option1")
                        .param("dimensionOptions[0].printingTech", "123")
                        .param("dimensionOptions[0].material", "123")
                        .param("dimensionOptions[0].dimensions[0].dimName", "dimName")
                        .param("dimensionOptions[0].dimensions[0].dimValue", String.valueOf(100.0))
                        .param("dimensionOptions[0].dimensions[0].dimUnit", "MM")
                        .cookie(userCookie)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.errors.size()").value(1));

        // Then
    }

    @Order(3)
    @DisplayName("[POST][ValidationError] 게시글 추가(치수옵션을 추가하지 않았을 경우) - 실패")
    @Test
    void addNewModelArticle_WithoutDimensionOption() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        // When
        mvc.perform(multipart(HttpMethod.POST, "/api/v1/articles")
                        .file(multipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("title", "title")
                        .param("content", "content")
                        .param("articleCategory", "MUSIC")
                        .cookie(userCookie)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.errors.size()").value(1));

        // Then
    }

    @Order(4)
    @DisplayName("[PUT] 게시글 수정 - 정상")
    @Test
    void updateRequestModelArticle() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        willDoNothing().given(articleService).updateArticle(any(), anyLong(), anyLong());
        // When
        mvc.perform(multipart(HttpMethod.PUT, "/api/v1/articles/1")
                        .file(multipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("title", "title")
                        .param("summary", "summary")
                        .param("content", "content")
                        .param("articleCategory", "MUSIC")
                        .param("dimensionOptions[0].optionName", "option1")
                        .param("dimensionOptions[0].printingTech", "123")
                        .param("dimensionOptions[0].material", "123")
                        .param("dimensionOptions[0].dimensions[0].dimName", "dimName")
                        .param("dimensionOptions[0].dimensions[0].dimValue", String.valueOf(100.0))
                        .param("dimensionOptions[0].dimensions[0].dimUnit", "MM")
                        .cookie(userCookie)
                        .with(csrf()))
                .andExpect(status().isOk());

        // Then
    }

    @Order(5)
    @DisplayName("[PUT][ValidationError] 게시글 수정(비어있는 파일) - 실패")
    @Test
    void updateRequestModelArticle_WithoutCategory() throws Exception {
        // Given
        // When
        mvc.perform(multipart(HttpMethod.PUT, "/api/v1/articles/1")
                        .file("modelFile", new byte[] {})
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("title", "title")
                        .param("summary", "summary")
                        .param("content", "content")
                        .param("articleCategory", "MUSIC")
                        .param("dimensionOptions[0].optionName", "option1")
                        .param("dimensionOptions[0].printingTech", "123")
                        .param("dimensionOptions[0].material", "123")
                        .param("dimensionOptions[0].dimensions[0].dimName", "dimName")
                        .param("dimensionOptions[0].dimensions[0].dimValue", String.valueOf(100.0))
                        .param("dimensionOptions[0].dimensions[0].dimUnit", "MM")
                        .cookie(userCookie)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.errors.size()").value(1));
        // Then
    }

    @Order(6)
    @DisplayName("[PUT][ValidationError] 게시글 수정(카테고리를 선택하지 않았을 경우) - 실패")
    @Test
    void updateRequestModelArticle_EmptyFile() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        // When
        mvc.perform(multipart(HttpMethod.PUT, "/api/v1/articles/1")
                        .file(multipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("title", "title")
                        .param("summary", "summary")
                        .param("content", "content")
                        .param("articleCategory", "카테고리를 선택해주세요.")
                        .param("dimensionOptions[0].optionName", "option1")
                        .param("dimensionOptions[0].printingTech", "123")
                        .param("dimensionOptions[0].material", "123")
                        .param("dimensionOptions[0].dimensions[0].dimName", "dimName")
                        .param("dimensionOptions[0].dimensions[0].dimValue", String.valueOf(100.0))
                        .param("dimensionOptions[0].dimensions[0].dimUnit", "MM")
                        .cookie(userCookie)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.errors.size()").value(1));
        // Then
    }

    @Order(7)
    @DisplayName("[PUT][ValidationError] 게시글 수정(치수옵션을 추가하지 않았을 경우) - 실패")
    @Test
    void updateRequestModelArticle_WithoutDimensionOption() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        // When
        mvc.perform(multipart(HttpMethod.PUT, "/api/v1/articles/1")
                        .file(multipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("title", "title")
                        .param("summary", "summary")
                        .param("content", "content")
                        .param("articleCategory", "MUSIC")
                        .cookie(userCookie)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.errors.size()").value(1));
        // Then
    }

    @Order(8)
    @DisplayName("[PUT] 게시글 수정(게시글 업데이트 실패) - 실패")
    @Test
    void updateRequestModelArticle_FailedArticleUpdate() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        willThrow(new ArticleException(ARTICLE_NOT_FOUND))
                .given(articleService)
                .updateArticle(any(), anyLong(), anyLong());
        // When
        mvc.perform(multipart(HttpMethod.PUT, "/api/v1/articles/1")
                        .file(multipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .param("title", "title")
                        .param("summary", "summary")
                        .param("content", "content")
                        .param("articleCategory", "MUSIC")
                        .param("dimensionOptions[0].optionName", "option1")
                        .param("dimensionOptions[0].printingTech", "123")
                        .param("dimensionOptions[0].material", "123")
                        .param("dimensionOptions[0].dimensions[0].dimName", "dimName")
                        .param("dimensionOptions[0].dimensions[0].dimValue", String.valueOf(100.0))
                        .param("dimensionOptions[0].dimensions[0].dimUnit", "MM")
                        .cookie(userCookie)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(ARTICLE_NOT_FOUND.getMessage()));
        // Then
    }

    @Order(9)
    @DisplayName("[DELETE] 게시글 삭제 - 정상")
    @Test
    void deleteModelArticle() throws Exception {
        // Given
        willDoNothing().given(articleFileService).deleteFile(anyLong());
        willDoNothing().given(articleService).deleteArticle(anyLong(), anyLong());
        // When
        mvc.perform(delete("/api/v1/articles/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .cookie(userCookie)
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty());
        // Then
    }

    @Order(10)
    @DisplayName("[DELETE] 게시글 삭제(게시글 삭제 실패) - 실패")
    @Test
    void deleteModelArticle_FailedDeleteArticle() throws Exception {
        // Given
        willDoNothing().given(articleFileService).deleteFile(anyLong());
        willThrow(new ArticleException(FAILED_DELETE)).given(articleService).deleteArticle(anyLong(), anyLong());
        // When
        mvc.perform(delete("/api/v1/articles/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .cookie(userCookie)
                        .with(csrf()))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(FAILED_DELETE.getMessage()));
        // Then
    }

    @Order(11)
    @DisplayName("[GET] 게시글 파일 다운로드 - 정상")
    @Test
    void downloadArticleFile() throws Exception {
        // given
        given(articleFileService.searchFileName(anyLong())).willReturn("fileName");
        given(articleFileService.download(anyString())).willReturn(new byte[] {1});
        // when
        mvc.perform(get("/api/v1/articles/1/download").cookie(userCookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists());
        // then
    }
}
