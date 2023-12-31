package joo.project.my3d.controller;

import com.querydsl.core.types.Predicate;
import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.DimensionOption;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.domain.constant.FormStatus;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.*;
import joo.project.my3d.dto.request.ArticleFormRequest;
import joo.project.my3d.dto.response.ArticleFormResponse;
import joo.project.my3d.dto.response.ArticleResponse;
import joo.project.my3d.dto.response.ArticleWithCommentsAndLikeCountResponse;
import joo.project.my3d.exception.ArticleException;
import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.exception.FileException;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.repository.ArticleLikeRepository;
import joo.project.my3d.service.AlarmService;
import joo.project.my3d.service.ArticleFileService;
import joo.project.my3d.service.ArticleService;
import joo.project.my3d.service.PaginationService;
import joo.project.my3d.service.aws.S3Service;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
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
    @WithMockUser
    @Test
    void modelArticles() throws Exception {
        // Given
        Pageable pageable = PageRequest.of(0, 9, Sort.by(Sort.Direction.DESC, "createdAt"));
        ArticlesDto articlesDto = FixtureDto.getArticlesDto();
        given(articleService.getArticles(any(Predicate.class), any(Pageable.class)))
                .willReturn(new PageImpl<>(List.of(articlesDto)));
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0));
        // When
        mvc.perform(
                get("/model_articles")
                        .cookie(Fixture.getCookie())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("model_articles/index"))
                .andExpect(model().attribute("articles", new PageImpl<>(List.of(ArticleResponse.from(articlesDto)), pageable, 1)))
                .andExpect(model().attributeExists("modelPath"))
                .andExpect(model().attribute("categories", ArticleCategory.values()))
                .andExpect(model().attribute("paginationBarNumbers", List.of(0)));

        // Then
        then(articleService).should().getArticles(any(Predicate.class), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @DisplayName("2. [GET] 게시글 페이지 - 정상")
    @Test
    void modelArticle() throws Exception {
        // Given
        given(articleLikeRepository.countByUserAccount_EmailAndArticle_Id(anyString(), anyLong()))
                .willReturn(2);
        ArticleWithCommentsAndLikeCountDto dto = FixtureDto.getArticleWithCommentsAndLikeCountDto(
                "title",
                "content",
                ArticleType.MODEL,
                ArticleCategory.ARCHITECTURE
        );
        given(articleService.getArticleWithComments(anyLong()))
                .willReturn(dto);
        ArticleWithCommentsAndLikeCountResponse article = ArticleWithCommentsAndLikeCountResponse.from(dto);
        // When
        mvc.perform(
                        get("/model_articles/1")
                                .cookie(Fixture.getCookie())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("model_articles/detail"))
                .andExpect(model().attribute("article", article))
                .andExpect(model().attribute("articleComments", article.articleCommentResponses()))
                .andExpect(model().attribute("modelFile", article.modelFile()))
                .andExpect(model().attribute("addedLike", true))
                .andExpect(model().attributeExists("modelPath"));

        // Then
        then(articleLikeRepository).should().countByUserAccount_EmailAndArticle_Id(anyString(), anyLong());
        then(articleService).should().getArticleWithComments(anyLong());
    }

    @DisplayName("3. [GET] 없는 게시글의 페이지를 요청할 경우 - 실패")
    @Test
    void failedGetModelArticle() throws Exception {
        // Given
        given(articleLikeRepository.countByUserAccount_EmailAndArticle_Id(anyString(), anyLong()))
                .willReturn(2);
        given(articleService.getArticleWithComments(anyLong()))
                .willThrow(new ArticleException(ErrorCode.ARTICLE_NOT_FOUND));
        // When
        mvc.perform(
                        get("/model_articles/1")
                                .cookie(Fixture.getCookie())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("model_articles/index"))
                .andExpect(model().attributeDoesNotExist("article"))
                .andExpect(model().attributeDoesNotExist("articleComments"))
                .andExpect(model().attributeDoesNotExist("modelFile"))
                .andExpect(model().attributeDoesNotExist("addedLike"))
                .andExpect(model().attributeDoesNotExist("modelPath"));

        // Then
        then(articleLikeRepository).should().countByUserAccount_EmailAndArticle_Id(anyString(), anyLong());
        then(articleService).should().getArticleWithComments(anyLong());
    }

    @DisplayName("4. [GET] 게시글 작성 페이지")
    @Test
    void writeNewModelArticle() throws Exception {
        // Given

        // When
        mvc.perform(
                    get("/model_articles/form")
                            .cookie(Fixture.getCookie())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("model_articles/form"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attribute("formStatus", FormStatus.CREATE))
                .andExpect(model().attribute("categories", ArticleCategory.values()));

        // Then
    }

    @DisplayName("5. [POST] 게시글 추가 - 정상")
    @Test
    void addNewModelArticle() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        Article article = Fixture.getArticle();
        given(articleService.saveArticle(anyString(), any(ArticleDto.class))).willReturn(article);
        willDoNothing().given(s3Service).uploadFile(any(), anyString());
        // When
        mvc.perform(
                    multipart("/model_articles/form")
                            .file(multipartFile)
                            .contentType(MediaType.MULTIPART_FORM_DATA)
                            .param("title", "title")
                            .param("content", "content")
                            .param("articleCategory", "MUSIC")
                            .param("dimensionOptions[0].optionName", "option1")
                            .param("dimensionOptions[0].dimensions[0].dimName", "dimName")
                            .param("dimensionOptions[0].dimensions[0].dimValue", String.valueOf(100.0))
                            .param("dimensionOptions[0].dimensions[0].dimUnit", "MM")
                            .cookie(Fixture.getCookie())
                            .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/model_articles"))
                .andExpect(redirectedUrl("/model_articles"));

        // Then
        then(articleService).should().saveArticle(anyString(), any(ArticleDto.class));
        then(s3Service).should().uploadFile(any(), anyString());
    }

    @DisplayName("6. [POST][ValidationError] 게시글 추가(비어있는 파일) - 실패")
    @Test
    void addNewModelArticle_WithoutFile() throws Exception {
        // Given
        // When
        mvc.perform(
                        multipart("/model_articles/form")
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
                                .cookie(Fixture.getCookie())
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("/model_articles/form"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attribute("formStatus", FormStatus.CREATE))
                .andExpect(model().attribute("categories", ArticleCategory.values()));

        // Then
    }

    @DisplayName("7. [POST][ValidationError] 게시글 추가(카테고리를 선택하지 않았을 경우) - 실패")
    @Test
    void addNewModelArticle_WithoutCategory() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        // When
        mvc.perform(
                        multipart("/model_articles/form")
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
                                .cookie(Fixture.getCookie())
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("/model_articles/form"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attribute("formStatus", FormStatus.CREATE))
                .andExpect(model().attribute("categories", ArticleCategory.values()));

        // Then
    }

    @DisplayName("8. [POST][ValidationError] 게시글 추가(치수옵션을 추가하지 않았을 경우) - 실패")
    @Test
    void addNewModelArticle_WithoutDimensionOption() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        // When
        mvc.perform(
                        multipart("/model_articles/form")
                                .file(multipartFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "title")
                                .param("content", "content")
                                .param("articleCategory", "MUSIC")
                                .cookie(Fixture.getCookie())
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("/model_articles/form"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attribute("formStatus", FormStatus.CREATE))
                .andExpect(model().attribute("categories", ArticleCategory.values()))
                .andExpect(model().attribute("dimensionOptionError", "상품 옵션 1개만 추가해주세요"));

        // Then
    }

    @DisplayName("9. [POST] 게시글 추가(S3 업로드에 실패할 경우) - 실패")
    @Test
    void addNewModelArticle_FailedS3Upload() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        Article article = Fixture.getArticle();
        given(articleService.saveArticle(anyString(), any(ArticleDto.class))).willReturn(article);
        willThrow(new IOException()).given(s3Service).uploadFile(any(), anyString());
        // When
        mvc.perform(
                        multipart("/model_articles/form")
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
                                .cookie(Fixture.getCookie())
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("/model_articles/form"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attribute("formStatus", FormStatus.CREATE))
                .andExpect(model().attribute("categories", ArticleCategory.values()));

        // Then
        then(articleService).should().saveArticle(anyString(), any(ArticleDto.class));
        then(s3Service).should().uploadFile(any(), anyString());
    }

    @DisplayName("10. [GET] 게시글 수정 페이지")
    @Test
    void updateModelArticle() throws Exception {
        // Given
        ArticleFormDto dto = FixtureDto.getArticleFormDto(
                "title",
                "content",
                ArticleType.MODEL,
                ArticleCategory.MUSIC
        );
        given(articleService.getArticle(anyLong())).willReturn(dto);
        // When
        mvc.perform(
                        get("/model_articles/form/1")
                                .cookie(Fixture.getCookie())

                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("model_articles/form"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attribute("formStatus", FormStatus.UPDATE))
                .andExpect(model().attribute("categories", ArticleCategory.values()));

        // Then
        then(articleService).should().getArticle(anyLong());
    }

    @DisplayName("11. [POST] 게시글 수정 - 정상")
    @Test
    void updateRequestModelArticle() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        willDoNothing().given(articleFileService).updateArticleFile(any(ArticleFormRequest.class), anyLong());
        willDoNothing().given(articleService).updateArticle(anyLong(), any(ArticleDto.class), anyString());
        // When
        mvc.perform(
                        multipart("/model_articles/form/1")
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
                                .cookie(Fixture.getCookie())
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/model_articles/1"))
                .andExpect(redirectedUrl("/model_articles/1"));

        // Then
        then(articleFileService).should().updateArticleFile(any(ArticleFormRequest.class), anyLong());
        then(articleService).should().updateArticle(anyLong(), any(ArticleDto.class), anyString());
    }

    @DisplayName("12. [POST][ValidationError] 게시글 수정(비어있는 파일) - 실패")
    @Test
    void updateRequestModelArticle_WithoutCategory() throws Exception {
        // Given
        // When
        mvc.perform(
                        multipart("/model_articles/form/1")
                                .file("modelFile", new byte[]{})
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
                                .cookie(Fixture.getCookie())
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("model_articles/form"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attribute("formStatus", FormStatus.UPDATE))
                .andExpect(model().attribute("categories", ArticleCategory.values()));
        // Then
    }

    @DisplayName("13. [POST][ValidationError] 게시글 수정(카테고리를 선택하지 않았을 경우) - 실패")
    @Test
    void updateRequestModelArticle_EmptyFile() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        // When
        mvc.perform(
                        multipart("/model_articles/form/1")
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
                                .cookie(Fixture.getCookie())
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("model_articles/form"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attribute("formStatus", FormStatus.UPDATE))
                .andExpect(model().attribute("categories", ArticleCategory.values()));
        // Then
    }

    @DisplayName("14. [POST][ValidationError] 게시글 수정(치수옵션을 추가하지 않았을 경우) - 실패")
    @Test
    void updateRequestModelArticle_WithoutDimensionOption() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        // When
        mvc.perform(
                        multipart("/model_articles/form/1")
                                .file(multipartFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "title")
                                .param("summary", "summary")
                                .param("content", "content")
                                .param("articleCategory", "MUSIC")
                                .cookie(Fixture.getCookie())
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("model_articles/form"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attribute("formStatus", FormStatus.UPDATE))
                .andExpect(model().attribute("categories", ArticleCategory.values()))
                .andExpect(model().attribute("dimensionOptionError", "상품 옵션 1개만 추가해주세요"));
        // Then
    }

    @DisplayName("15. [POST] 게시글 수정(파일 업데이트 실패) - 실패")
    @Test
    void updateRequestModelArticle_FailedFileUpdate() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        willThrow(new FileException(ErrorCode.FAILED_DELETE)).given(articleFileService).updateArticleFile(any(ArticleFormRequest.class), anyLong());
        // When
        mvc.perform(
                        multipart("/model_articles/form/1")
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
                                .cookie(Fixture.getCookie())
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("model_articles/form"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attribute("formStatus", FormStatus.UPDATE))
                .andExpect(model().attribute("categories", ArticleCategory.values()));
        // Then
        then(articleFileService).should().updateArticleFile(any(ArticleFormRequest.class), anyLong());
    }

    @DisplayName("16. [POST] 게시글 수정(게시글 업데이트 실패) - 실패")
    @Test
    void updateRequestModelArticle_FailedArticleUpdate() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        willDoNothing().given(articleFileService).updateArticleFile(any(ArticleFormRequest.class), anyLong());
        willThrow(new ArticleException(ErrorCode.ARTICLE_NOT_FOUND)).given(articleService).updateArticle(anyLong(), any(ArticleDto.class), anyString());
        // When
        mvc.perform(
                        multipart("/model_articles/form/1")
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
                                .cookie(Fixture.getCookie())
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("model_articles/form"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attribute("formStatus", FormStatus.UPDATE))
                .andExpect(model().attribute("categories", ArticleCategory.values()));
        // Then
        then(articleFileService).should().updateArticleFile(any(ArticleFormRequest.class), anyLong());
        then(articleService).should().updateArticle(anyLong(), any(ArticleDto.class), anyString());
    }

    @DisplayName("17. [POST] 게시글 삭제 - 정상")
    @Test
    void deleteModelArticle() throws Exception {
        // Given
        willDoNothing().given(articleFileService).deleteArticleFile(anyLong());
        willDoNothing().given(articleService).deleteArticle(anyLong(), anyString());
        // When
        mvc.perform(
                        post("/model_articles/1/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .cookie(Fixture.getCookie())
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/model_articles"))
                .andExpect(redirectedUrl("/model_articles"));
        // Then
        then(articleFileService).should().deleteArticleFile(anyLong());
        then(articleService).should().deleteArticle(anyLong(), anyString());
    }

    @DisplayName("18. [POST] 게시글 삭제(게시글 삭제 실패) - 실패")
    @Test
    void deleteModelArticle_FailedDeleteArticle() throws Exception {
        // Given
        willDoNothing().given(articleFileService).deleteArticleFile(anyLong());
        willThrow(new ArticleException(ErrorCode.FAILED_DELETE)).given(articleService).deleteArticle(anyLong(), anyString());
        // When
        mvc.perform(
                        post("/model_articles/1/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .cookie(Fixture.getCookie())
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/model_articles/1"))
                .andExpect(redirectedUrl("/model_articles/1"));
        // Then
        then(articleFileService).should().deleteArticleFile(anyLong());
        then(articleService).should().deleteArticle(anyLong(), anyString());
    }

    @DisplayName("19. [POST] 게시글 삭제(게시글 파일 삭제 실패) - 실패")
    @Test
    void deleteModelArticle_FailedDeleteArticleFile() throws Exception {
        // Given
        willThrow(new FileException(ErrorCode.FAILED_DELETE)).given(articleFileService).deleteArticleFile(anyLong());
        // When
        mvc.perform(
                        post("/model_articles/1/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .cookie(Fixture.getCookie())
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/model_articles/1"))
                .andExpect(redirectedUrl("/model_articles/1"));
        // Then
        then(articleFileService).should().deleteArticleFile(anyLong());
    }

    @DisplayName("20. [GET] 게시글 파일 다운로드 - 정상")
    @Test
    void downloadArticleFile() throws Exception {
        //given
        ArticleFileDto articleFileDto = FixtureDto.getArticleFileDto();
        given(articleFileService.getArticleFile(anyLong())).willReturn(articleFileDto);
        given(s3Service.downloadFile(anyString())).willReturn(new byte[]{1});
        //when
        mvc.perform(
                get("/model_articles/1/download")
                        .cookie(Fixture.getCookie())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(header().string("Content-Length", "1"))
                .andExpect(header().string("Content-Disposition", String.format("form-data; name=\"attachment\"; filename=\"%s\"", articleFileDto.originalFileName())))
                ;
        //then
        then(articleFileService).should().getArticleFile(anyLong());
        then(s3Service).should().downloadFile(anyString());
    }
}
