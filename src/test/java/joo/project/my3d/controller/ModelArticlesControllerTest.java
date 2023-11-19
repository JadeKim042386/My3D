package joo.project.my3d.controller;

import com.querydsl.core.types.Predicate;
import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.DimensionOption;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.*;
import joo.project.my3d.dto.request.ArticleFormRequest;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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
    @MockBean private AlarmService alarmService;
    @MockBean private S3Service s3Service;

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
                        .cookie(Fixture.getCookie())
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("model_articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attributeExists("modelPath"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("paginationBarNumbers"));

        // Then
        then(articleService).should().getArticles(any(Predicate.class), any(Pageable.class));
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }

    @DisplayName("[GET] 게시글 작성 페이지")
    @Test
    void writeNewModelArticle() throws Exception {
        // Given

        // When
        mvc.perform(
                        get("/model_articles/form")
                                .with(user("jooCompany").password("pw").roles("COMPANY"))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("model_articles/form"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("formStatus"))
                .andExpect(model().attributeExists("categories"));

        // Then
    }

    @DisplayName("[POST] 게시글 추가 - 정상")
    @Test
    void addNewModelArticle() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        Article article = Fixture.getArticle();
        given(articleFileService.saveArticleFileWithForm(any(ArticleFormRequest.class))).willReturn(Fixture.getArticleFile());
        given(articleService.saveArticle(any(ArticleDto.class))).willReturn(article);
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("jooCompany", UserRole.COMPANY);
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
                                .with(authentication(authentication))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/model_articles"))
                .andExpect(redirectedUrl("/model_articles"));

        // Then
        then(articleService).should().saveArticle(any(ArticleDto.class));
        then(articleFileService).should().saveArticleFileWithForm(any(ArticleFormRequest.class));
    }

    @DisplayName("[POST] 게시글 추가 - 파일 누락")
    @Test
    void addNewModelArticleWithoutFile() throws Exception {
        // Given
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("jooCompany", UserRole.COMPANY);
        // When
        mvc.perform(
                        multipart("/model_articles/form")
                                .file("modelFile", null)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "")
                                .param("content", "content")
                                .param("articleCategory", "카테고리를 선택해주세요.")
                                .param("dimensionOptions[0].optionName", "option1")
                                .param("dimensionOptions[0].printingTech", "123")
                                .param("dimensionOptions[0].material", "123")
                                .param("dimensionOptions[0].dimensions[0].dimName", "dimName")
                                .param("dimensionOptions[0].dimensions[0].dimValue", String.valueOf(100.0))
                                .param("dimensionOptions[0].dimensions[0].dimUnit", "MM")
                                .with(authentication(authentication))
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("/model_articles/form"));

        // Then
    }

    @DisplayName("[POST] 게시글 추가 - 카테고리 누락")
    @Test
    void addNewModelArticleWithoutCategory() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("jooCompany", UserRole.COMPANY);
        // When
        mvc.perform(
                        multipart("/model_articles/form")
                                .file(multipartFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "")
                                .param("content", "content")
                                .param("articleCategory", "카테고리를 선택해주세요.")
                                .param("dimensionOptions[0].optionName", "option1")
                                .param("dimensionOptions[0].printingTech", "123")
                                .param("dimensionOptions[0].material", "123")
                                .param("dimensionOptions[0].dimensions[0].dimName", "dimName")
                                .param("dimensionOptions[0].dimensions[0].dimValue", String.valueOf(100.0))
                                .param("dimensionOptions[0].dimensions[0].dimUnit", "MM")
                                .with(authentication(authentication))
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("/model_articles/form"));

        // Then
    }

    @DisplayName("[POST] 게시글 추가 - 제목 누락")
    @Test
    void addNewModelArticleWithoutTitle() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("jooCompany", UserRole.COMPANY);
        // When
        mvc.perform(
                        multipart("/model_articles/form")
                                .file(multipartFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "")
                                .param("content", "content")
                                .param("articleCategory", "MUSIC")
                                .param("dimensionOptions[0].optionName", "option1")
                                .param("dimensionOptions[0].printingTech", "123")
                                .param("dimensionOptions[0].material", "123")
                                .param("dimensionOptions[0].dimensions[0].dimName", "dimName")
                                .param("dimensionOptions[0].dimensions[0].dimValue", String.valueOf(100.0))
                                .param("dimensionOptions[0].dimensions[0].dimUnit", "MM")
                                .with(authentication(authentication))
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("/model_articles/form"));

        // Then
    }

    @DisplayName("[POST] 게시글 추가 - 본문 누락")
    @Test
    void addNewModelArticleWithoutContent() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("jooCompany", UserRole.COMPANY);
        // When
        mvc.perform(
                        multipart("/model_articles/form")
                                .file(multipartFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "title")
                                .param("content", "")
                                .param("articleCategory", "MUSIC")
                                .param("dimensionOptions[0].optionName", "option1")
                                .param("dimensionOptions[0].printingTech", "123")
                                .param("dimensionOptions[0].material", "123")
                                .param("dimensionOptions[0].dimensions[0].dimName", "dimName")
                                .param("dimensionOptions[0].dimensions[0].dimValue", String.valueOf(100.0))
                                .param("dimensionOptions[0].dimensions[0].dimUnit", "MM")
                                .with(authentication(authentication))
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("/model_articles/form"));

        // Then
    }

    @DisplayName("[POST] 게시글 추가 - 상품옵션 누락")
    @Test
    void addNewModelArticleWithoutDimensionOption() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("jooCompany", UserRole.COMPANY);
        // When
        mvc.perform(
                        multipart("/model_articles/form")
                                .file(multipartFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "title")
                                .param("content", "content")
                                .param("articleCategory", "MUSIC")
                                .with(authentication(authentication))
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("/model_articles/form"));

        // Then
    }

    @DisplayName("[GET] 게시글 페이지")
    @Test
    void modelArticle() throws Exception {
        // Given
        Long articleId = 1L;
        given(articleLikeRepository.countByUserAccount_EmailAndArticle_Id("jooUser@gmail.com", articleId)).willReturn(2);
        given(articleService.getArticleWithComments(articleId)).willReturn(
                FixtureDto.getArticleWithCommentsAndLikeCountDto("title", "content", ArticleType.MODEL, ArticleCategory.ARCHITECTURE)
        );
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("jooUser", UserRole.USER);
        // When
        mvc.perform(
                        get("/model_articles/1")
                                .cookie(Fixture.getCookie())
                                .with(authentication(authentication))
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("model_articles/detail"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("articleComments"))
                .andExpect(model().attributeExists("modelFile"))
                .andExpect(model().attributeExists("addedLike"))
                .andExpect(model().attributeExists("modelPath"));

        // Then
        then(articleLikeRepository).should().countByUserAccount_EmailAndArticle_Id("jooUser@gmail.com", articleId);
        then(articleService).should().getArticleWithComments(articleId);
    }

    @DisplayName("[GET] 게시글 수정 페이지")
    @Test
    void updateModelArticle() throws Exception {
        // Given
        ArticleFormDto dto = FixtureDto.getArticleFormDto("title", "content", ArticleType.MODEL, ArticleCategory.MUSIC);
        given(articleService.getArticle(anyLong())).willReturn(dto);
        // When
        mvc.perform(
                        get("/model_articles/form/1")
                                .with(user("jooCompany").password("pw").roles("COMPANY"))

                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("model_articles/form"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("formStatus"))
                .andExpect(model().attributeExists("categories"));

        // Then
        then(articleService).should().getArticle(anyLong());
    }

    @DisplayName("[POST] 게시글 수정 - 정상")
    @Test
    void updateRequestModelArticle() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        Article article = Fixture.getArticle();
        FieldUtils.writeField(article, "id", 1L, true);
        DimensionOption dimensionOption = Fixture.getDimensionOption();
        FieldUtils.writeField(dimensionOption, "id", 1L, true);
        willDoNothing().given(articleFileService).updateArticleFile(any(ArticleFormRequest.class), eq(article.getId()));
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("jooCompany", UserRole.COMPANY);
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
                                .with(authentication(authentication))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/model_articles/1"))
                .andExpect(redirectedUrl("/model_articles/1"));

        // Then
        then(articleFileService).should().updateArticleFile(any(ArticleFormRequest.class), eq(article.getId()));
    }

    @DisplayName("[POST] 게시글 수정 - 권한이 없는 User가 요청")
    @Test
    void updateRequestModelArticleByUser() throws Exception {
        // Given
        byte[] file = Fixture.getMultipartFile().getBytes();
        // When
        mvc.perform(
                        multipart("/model_articles/form/1")
                                .file("file", file)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "title")
                                .param("content", "content")
                                .param("articleCategory", "MUSIC")
                                .with(user("jooUser").password("pw").roles("USER"))

                )
                .andExpect(status().isForbidden());

        // Then
    }

    @DisplayName("[POST] 게시글 삭제 - 정상")
    @Test
    void deleteModelArticle() throws Exception {
        // Given
        willDoNothing().given(articleService).deleteArticle(anyLong(), anyString());
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("userCompany", UserRole.COMPANY);
        // When
        mvc.perform(
                        post("/model_articles/1/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .with(authentication(authentication))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/model_articles"))
                .andExpect(redirectedUrl("/model_articles"));
        // Then
        then(articleService).should().deleteArticle(anyLong(), anyString());
    }

    @DisplayName("[POST] 게시글 삭제 - 권한이 없는 User가 요청")
    @Test
    void deleteModelArticleByUser() throws Exception {
        // Given

        // When
        mvc.perform(
                        post("/model_articles/1/delete")
                                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                                .with(user("jooUser").password("pw").roles("USER"))
                )
                .andExpect(status().isForbidden());
        // Then
    }

    @DisplayName("[GET] 게시글 파일 다운로드 - 정상")
    @Test
    void downloadArticleFile() throws Exception {
        //given
        Long articleId = 1L;
        ArticleFileDto articleFileDto = FixtureDto.getArticleFileDto();
        given(articleFileService.getArticleFile(articleId)).willReturn(articleFileDto);
        String fileName = articleFileDto.fileName();
        given(s3Service.downloadFile(fileName)).willReturn(new byte[]{1});
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("jooCompany", UserRole.COMPANY);
        //when
        mvc.perform(
                get("/model_articles/1/download")
                        .with(authentication(authentication))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(header().string("Content-Length", "1"))
                .andExpect(header().string("Content-Disposition", String.format("form-data; name=\"attachment\"; filename=\"%s\"", articleFileDto.originalFileName())))
                ;
        //then
        then(articleFileService).should().getArticleFile(articleId);
        then(s3Service).should().downloadFile(fileName);
    }
}
