package joo.project.my3d.controller;

import com.querydsl.core.types.Predicate;
import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.domain.constant.FormStatus;
import joo.project.my3d.dto.ArticleDto;
import joo.project.my3d.dto.ArticleFormDto;
import joo.project.my3d.dto.ArticlePreviewDto;
import joo.project.my3d.dto.ArticleWithCommentsDto;
import joo.project.my3d.exception.ArticleException;
import joo.project.my3d.exception.FileException;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.repository.ArticleLikeRepository;
import joo.project.my3d.service.ArticleFileService;
import joo.project.my3d.service.ArticleService;
import joo.project.my3d.service.PaginationService;
import joo.project.my3d.service.aws.S3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static joo.project.my3d.exception.constant.ErrorCode.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@DisplayName("View 컨트롤러 - 게시글")
@Import(TestSecurityConfig.class)
@WebMvcTest(ModelArticlesController.class)
class ModelArticlesControllerTest {

    @Autowired private MockMvc mvc;
    @MockBean private ArticleService articleService;
    @MockBean private PaginationService paginationService;
    @MockBean private ArticleFileService articleFileService;
    @MockBean private ArticleLikeRepository articleLikeRepository;
    @MockBean private S3Service s3Service;

    @DisplayName("1. [GET] 게시판 페이지(게시글이 존재할 경우) - 정상")
    @Test
    void modelArticles() throws Exception {
        // Given
        ArticlePreviewDto articlesDto = FixtureDto.getArticlesDto();
        given(articleService.getArticlesForPreview(any(Predicate.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(articlesDto)));
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0));
        // When
        mvc.perform(
                get("/model_articles")
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.articles.totalElements").value(1))
                .andExpect(jsonPath("$.modelPath").exists())
                .andExpect(jsonPath("$.categories.length()").value(ArticleCategory.values().length))
                .andExpect(jsonPath("$.barNumbers.size()").value(1));

        // Then
    }

    @DisplayName("2. [GET] 게시글 페이지 - 정상")
    @WithUserDetails(value = "jooUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void modelArticle() throws Exception {
        // Given
        given(articleLikeRepository.existsByArticleIdAndUserAccount_Email(anyLong(), anyString()))
                .willReturn(true);
        given(articleLikeRepository.countByArticleId(anyLong())).willReturn(2);
        ArticleWithCommentsDto dto = FixtureDto.getArticleWithCommentsAndLikeCountDto(
                "title",
                "content",
                ArticleType.MODEL,
                ArticleCategory.ARCHITECTURE
        );
        given(articleService.getArticleWithComments(anyLong()))
                .willReturn(dto);
        // When
        mvc.perform(
                        get("/model_articles/1")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.article.title").value("title"))
                .andExpect(jsonPath("$.article.content").value("content"))
                .andExpect(jsonPath("$.article.articleType").value(ArticleType.MODEL.toString()))
                .andExpect(jsonPath("$.article.articleCategory").value(ArticleCategory.ARCHITECTURE.toString()))
                .andExpect(jsonPath("$.likeCount").value(2))
                .andExpect(jsonPath("$.addedLike").value(true))
                .andExpect(jsonPath("$.modelPath").exists());

        // Then
    }

    @DisplayName("3. [GET] 게시글 작성 페이지")
    @WithUserDetails(value = "jooUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void writeNewModelArticle() throws Exception {
        // Given

        // When
        mvc.perform(
                    get("/model_articles/add")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.formStatus").value(FormStatus.CREATE.toString()))
                .andExpect(jsonPath("$.categories.length()").value(ArticleCategory.values().length));
        // Then
    }

    @DisplayName("4. [POST] 게시글 추가 - 정상")
    @WithUserDetails(value = "jooUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void addNewModelArticle() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        Article article = Fixture.getArticle();
        given(articleService.saveArticle(anyString(), any(ArticleDto.class))).willReturn(article);
        willDoNothing().given(s3Service).uploadFile(any(), anyString());
        // When
        mvc.perform(
                    multipart(HttpMethod.POST, "/model_articles/add")
                            .file(multipartFile)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .param("title", "title")
                            .param("content", "content")
                            .param("articleCategory", "MUSIC")
                            .param("dimensionOptions[0].optionName", "option1")
                            .param("dimensionOptions[0].dimensions[0].dimName", "dimName")
                            .param("dimensionOptions[0].dimensions[0].dimValue", String.valueOf(100.0))
                            .param("dimensionOptions[0].dimensions[0].dimUnit", "MM")
                            .with(csrf())
                )
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty())
        ;

        // Then
    }

    @DisplayName("5. [POST][ValidationError] 게시글 추가(비어있는 파일) - 실패")
    @WithUserDetails(value = "jooUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void addNewModelArticle_WithoutFile() throws Exception {
        // Given
        // When
        mvc.perform(
                        multipart(HttpMethod.POST, "/model_articles/add")
                                .file("modelFile", new byte[]{})
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
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.formStatus").value(FormStatus.CREATE.toString()))
                .andExpect(jsonPath("$.categories.length()").value(ArticleCategory.values().length))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.articleCategory").value("MUSIC"))
                .andExpect(jsonPath("$.modelFile.dimensionOption.optionName").value("option1"))
                .andExpect(jsonPath("$.modelFile.dimensionOption.dimensionDtos[0].dimName").value("dimName"))
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.validMessages").isNotEmpty());

        // Then
    }

    @DisplayName("6. [POST][ValidationError] 게시글 추가(카테고리를 선택하지 않았을 경우) - 실패")
    @WithUserDetails(value = "jooUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void addNewModelArticle_WithoutCategory() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        // When
        mvc.perform(
                        multipart(HttpMethod.POST, "/model_articles/add")
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
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.formStatus").value(FormStatus.CREATE.toString()))
                .andExpect(jsonPath("$.categories.length()").value(ArticleCategory.values().length))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.articleCategory").value("카테고리를 선택해주세요."))
                .andExpect(jsonPath("$.modelFile.dimensionOption.optionName").value("option1"))
                .andExpect(jsonPath("$.modelFile.dimensionOption.dimensionDtos[0].dimName").value("dimName"))
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.validMessages").isNotEmpty());

        // Then
    }

    @DisplayName("7. [POST][ValidationError] 게시글 추가(치수옵션을 추가하지 않았을 경우) - 실패")
    @WithUserDetails(value = "jooUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void addNewModelArticle_WithoutDimensionOption() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        // When
        mvc.perform(
                        multipart(HttpMethod.POST, "/model_articles/add")
                                .file(multipartFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "title")
                                .param("content", "content")
                                .param("articleCategory", "MUSIC")
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.formStatus").value(FormStatus.CREATE.toString()))
                .andExpect(jsonPath("$.categories.length()").value(ArticleCategory.values().length))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.articleCategory").value("MUSIC"))
                .andExpect(jsonPath("$.modelFile.dimensionOption").isEmpty())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.validMessages").isNotEmpty());

        // Then
    }

    @DisplayName("8. [POST] 게시글 추가(S3 업로드에 실패할 경우) - 실패")
    @WithUserDetails(value = "jooUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void addNewModelArticle_FailedS3Upload() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        Article article = Fixture.getArticle();
        given(articleService.saveArticle(anyString(), any(ArticleDto.class))).willReturn(article);
        willThrow(new FileException(FILE_CANT_SAVE)).given(s3Service).uploadFile(any(), anyString());
        // When
        mvc.perform(
                        multipart(HttpMethod.POST, "/model_articles/add")
                                .file(multipartFile)
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
                                .with(csrf())
                )
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(FILE_CANT_SAVE.getMessage()));

        // Then
    }

    @DisplayName("10. [GET] 게시글 수정 페이지")
    @WithUserDetails(value = "jooUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void updateModelArticle() throws Exception {
        // Given
        ArticleFormDto dto = FixtureDto.getArticleFormDto(
                "title",
                "content",
                ArticleType.MODEL,
                ArticleCategory.MUSIC
        );
        given(articleService.getArticleForm(anyLong())).willReturn(dto);
        // When
        mvc.perform(
                        get("/model_articles/update/1")
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.formStatus").value(FormStatus.UPDATE.toString()))
                .andExpect(jsonPath("$.categories.length()").value(ArticleCategory.values().length))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.articleCategory").value("MUSIC"))
                ;

        // Then
    }

    @DisplayName("11. [PUT] 게시글 수정 - 정상")
    @WithUserDetails(value = "jooUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void updateRequestModelArticle() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        willDoNothing().given(articleService).updateArticle(any(), anyLong(), anyString());
        // When
        mvc.perform(
                        multipart(HttpMethod.PUT, "/model_articles/update/1")
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
                                .with(csrf())
                )
                .andExpect(status().isOk())
                ;

        // Then
    }

    @DisplayName("12. [PUT][ValidationError] 게시글 수정(비어있는 파일) - 실패")
    @WithUserDetails(value = "jooUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void updateRequestModelArticle_WithoutCategory() throws Exception {
        // Given
        // When
        mvc.perform(
                        multipart(HttpMethod.PUT, "/model_articles/update/1")
                                .file("modelFile", new byte[]{})
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
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.formStatus").value(FormStatus.UPDATE.toString()))
                .andExpect(jsonPath("$.categories.length()").value(ArticleCategory.values().length))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.articleCategory").value("MUSIC"))
                .andExpect(jsonPath("$.modelFile.dimensionOption.optionName").value("option1"))
                .andExpect(jsonPath("$.modelFile.dimensionOption.dimensionDtos[0].dimName").value("dimName"))
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.validMessages").isNotEmpty())
                ;
        // Then
    }

    @DisplayName("13. [PUT][ValidationError] 게시글 수정(카테고리를 선택하지 않았을 경우) - 실패")
    @WithUserDetails(value = "jooUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void updateRequestModelArticle_EmptyFile() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        // When
        mvc.perform(
                        multipart(HttpMethod.PUT, "/model_articles/update/1")
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
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.formStatus").value(FormStatus.UPDATE.toString()))
                .andExpect(jsonPath("$.categories.length()").value(ArticleCategory.values().length))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.articleCategory").value("카테고리를 선택해주세요."))
                .andExpect(jsonPath("$.modelFile.dimensionOption.optionName").value("option1"))
                .andExpect(jsonPath("$.modelFile.dimensionOption.dimensionDtos[0].dimName").value("dimName"))
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.validMessages").isNotEmpty())
        ;
        // Then
    }

    @DisplayName("14. [PUT][ValidationError] 게시글 수정(치수옵션을 추가하지 않았을 경우) - 실패")
    @WithUserDetails(value = "jooUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void updateRequestModelArticle_WithoutDimensionOption() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        // When
        mvc.perform(
                        multipart(HttpMethod.PUT, "/model_articles/update/1")
                                .file(multipartFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "title")
                                .param("summary", "summary")
                                .param("content", "content")
                                .param("articleCategory", "MUSIC")
                                .with(csrf())
                )
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.formStatus").value(FormStatus.UPDATE.toString()))
                .andExpect(jsonPath("$.categories.length()").value(ArticleCategory.values().length))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.content").value("content"))
                .andExpect(jsonPath("$.articleCategory").value("MUSIC"))
                .andExpect(jsonPath("$.modelFile.dimensionOption").isEmpty())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.validMessages").isNotEmpty())
        ;
        // Then
    }


    @DisplayName("16. [PUT] 게시글 수정(게시글 업데이트 실패) - 실패")
    @WithUserDetails(value = "jooUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void updateRequestModelArticle_FailedArticleUpdate() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        willThrow(new ArticleException(ARTICLE_NOT_FOUND)).given(articleService).updateArticle(any(), anyLong(), anyString());
        // When
        mvc.perform(
                        multipart(HttpMethod.PUT, "/model_articles/update/1")
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
                                .with(csrf())
                )
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(ARTICLE_NOT_FOUND.getMessage()))
        ;
        // Then
    }

    @DisplayName("17. [DELETE] 게시글 삭제 - 정상")
    @WithUserDetails(value = "jooUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void deleteModelArticle() throws Exception {
        // Given
        willDoNothing().given(articleFileService).deleteArticleFile(anyLong());
        willDoNothing().given(articleService).deleteArticle(anyLong(), anyString());
        // When
        mvc.perform(
                        delete("/model_articles/1")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .with(csrf())
                )
                .andExpect(status().isNoContent())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").isNotEmpty());
        // Then
    }

    @DisplayName("18. [DELETE] 게시글 삭제(게시글 삭제 실패) - 실패")
    @WithUserDetails(value = "jooUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void deleteModelArticle_FailedDeleteArticle() throws Exception {
        // Given
        willDoNothing().given(articleFileService).deleteArticleFile(anyLong());
        willThrow(new ArticleException(FAILED_DELETE)).given(articleService).deleteArticle(anyLong(), anyString());
        // When
        mvc.perform(
                        delete("/model_articles/1")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .with(csrf())
                )
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(FAILED_DELETE.getMessage()));
        // Then
    }

    @DisplayName("20. [GET] 게시글 파일 다운로드 - 정상")
    @WithUserDetails(value = "jooUser@gmail.com", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Test
    void downloadArticleFile() throws Exception {
        //given
        given(articleFileService.download(anyLong())).willReturn(new byte[]{1});
        //when
        mvc.perform(
                get("/model_articles/download/1")
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").exists())
                ;
        //then
    }
}
