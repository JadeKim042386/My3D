package joo.project.my3d.controller;

import com.querydsl.core.types.Predicate;
import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.domain.*;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.*;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.repository.ArticleLikeRepository;
import joo.project.my3d.service.*;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("View 컨트롤러 - 게시글")
@Import(TestSecurityConfig.class)
@WebMvcTest(ModelArticlesController.class)
class ModelArticlesControllerTest {

    @Autowired private MockMvc mvc;
    @MockBean private ArticleService articleService;
    @MockBean private PaginationService paginationService;
    @MockBean private ArticleFileService articleFileService;
    @MockBean private ArticleLikeRepository articleLikeRepository;
    @MockBean private GoodOptionService goodOptionService;
    @MockBean private DimensionService dimensionService;
    @MockBean private AlarmService alarmService;

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
        GoodOption goodOption = Fixture.getGoodOption(article);
        given(articleService.saveArticle(any(ArticleDto.class))).willReturn(article);
        given(articleFileService.saveArticleFile(multipartFile)).willReturn(Fixture.getArticleFile());
        given(goodOptionService.saveGoodOption(any(GoodOptionDto.class))).willReturn(goodOption);
        willDoNothing().given(dimensionService).saveDimension(any(DimensionDto.class));
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("jooCompany", UserRole.COMPANY);
        // When
        mvc.perform(
                        multipart("/model_articles/form")
                                .file(multipartFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "title")
                                .param("summary", "summary")
                                .param("content", "content")
                                .param("articleCategory", "MUSIC")
                                .param("goodOptions[0].optionName", "option1")
                                .param("goodOptions[0].printingTech", "123")
                                .param("goodOptions[0].material", "123")
                                .param("goodOptions[0].dimensions[0].dimName", "dimName")
                                .param("goodOptions[0].dimensions[0].dimValue", String.valueOf(100.0))
                                .param("goodOptions[0].dimensions[0].dimUnit", "MM")
                                .with(authentication(authentication))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/model_articles"))
                .andExpect(redirectedUrl("/model_articles"));

        // Then
        then(articleFileService).should().saveArticleFile(multipartFile);
        then(articleService).should().saveArticle(any(ArticleDto.class));
        then(goodOptionService).should().saveGoodOption(any(GoodOptionDto.class));
        then(dimensionService).should().saveDimension(any(DimensionDto.class));
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
                                .param("summary", "summary")
                                .param("content", "content")
                                .param("articleCategory", "카테고리를 선택해주세요.")
                                .param("goodOptions[0].optionName", "option1")
                                .param("goodOptions[0].printingTech", "123")
                                .param("goodOptions[0].material", "123")
                                .param("goodOptions[0].dimensions[0].dimName", "dimName")
                                .param("goodOptions[0].dimensions[0].dimValue", String.valueOf(100.0))
                                .param("goodOptions[0].dimensions[0].dimUnit", "MM")
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
                                .param("summary", "summary")
                                .param("content", "content")
                                .param("articleCategory", "카테고리를 선택해주세요.")
                                .param("goodOptions[0].optionName", "option1")
                                .param("goodOptions[0].printingTech", "123")
                                .param("goodOptions[0].material", "123")
                                .param("goodOptions[0].dimensions[0].dimName", "dimName")
                                .param("goodOptions[0].dimensions[0].dimValue", String.valueOf(100.0))
                                .param("goodOptions[0].dimensions[0].dimUnit", "MM")
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
                                .param("summary", "summary")
                                .param("content", "content")
                                .param("articleCategory", "MUSIC")
                                .param("goodOptions[0].optionName", "option1")
                                .param("goodOptions[0].printingTech", "123")
                                .param("goodOptions[0].material", "123")
                                .param("goodOptions[0].dimensions[0].dimName", "dimName")
                                .param("goodOptions[0].dimensions[0].dimValue", String.valueOf(100.0))
                                .param("goodOptions[0].dimensions[0].dimUnit", "MM")
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
                                .param("summary", "summary")
                                .param("content", "")
                                .param("articleCategory", "MUSIC")
                                .param("goodOptions[0].optionName", "option1")
                                .param("goodOptions[0].printingTech", "123")
                                .param("goodOptions[0].material", "123")
                                .param("goodOptions[0].dimensions[0].dimName", "dimName")
                                .param("goodOptions[0].dimensions[0].dimValue", String.valueOf(100.0))
                                .param("goodOptions[0].dimensions[0].dimUnit", "MM")
                                .with(authentication(authentication))
                                .with(csrf())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("/model_articles/form"));

        // Then
    }

    @DisplayName("[POST] 게시글 추가 - 상품옵션 누락")
    @Test
    void addNewModelArticleWithoutGoodOption() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("jooCompany", UserRole.COMPANY);
        // When
        mvc.perform(
                        multipart("/model_articles/form")
                                .file(multipartFile)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "title")
                                .param("summary", "summary")
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
        ArticleWithCommentsAndLikeCountDto dto = FixtureDto.getArticleWithCommentsAndLikeCountDto("title", "summary", "content", ArticleType.MODEL, ArticleCategory.ARCHITECTURE);
        UserAccountDto userAccountDto = FixtureDto.getUserAccountDto("jooUser", UserRole.USER, true);
        UserAccount userAccount = userAccountDto.toEntity();
        ArticleLike articleLike = Fixture.getArticleLike(userAccount);
        given(articleLikeRepository.findByUserAccount_EmailAndArticle_Id(articleLike.getUserAccount().getEmail(), articleId)).willReturn(Optional.of(articleLike));
        given(articleService.getArticleWithComments(articleId)).willReturn(dto);
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("jooUser", UserRole.USER);
        // When
        mvc.perform(
                        get("/model_articles/1")
                                .with(authentication(authentication))
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
        then(articleLikeRepository).should().findByUserAccount_EmailAndArticle_Id(articleLike.getUserAccount().getEmail(), articleId);
        then(articleService).should().getArticleWithComments(articleId);
    }

    @DisplayName("[GET] 게시글 수정 페이지")
    @Test
    void updateModelArticle() throws Exception {
        // Given
        ArticleDto articleDto = FixtureDto.getArticleDto(1L, "title", "summary", "content", ArticleType.MODEL, ArticleCategory.MUSIC);
        ArticleFileDto articleFileDto = FixtureDto.getArticleFileDto();
        given(goodOptionService.getGoodOptionWithDimensions(anyLong())).willReturn(List.of());
        given(articleService.getArticle(anyLong())).willReturn(articleDto);
        given(articleFileService.getArticleFile(anyLong())).willReturn(articleFileDto);
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
        then(goodOptionService).should().getGoodOptionWithDimensions(anyLong());
        then(articleService).should().getArticle(anyLong());
        then(articleFileService).should().getArticleFile(anyLong());
    }

    @DisplayName("[POST] 게시글 수정 - 정상")
    @Test
    void updateRequestModelArticle() throws Exception {
        // Given
        MockMultipartFile multipartFile = Fixture.getMultipartFile();
        Article article = Fixture.getArticle();
        FieldUtils.writeField(article, "id", 1L, true);
        GoodOption goodOption = Fixture.getGoodOption(article);
        FieldUtils.writeField(goodOption, "id", 1L, true);
        given(articleFileService.getArticleFile(anyLong())).willReturn(FixtureDto.getArticleFileDto());
        given(articleFileService.updateArticleFile(eq(multipartFile))).willReturn(true);
        willDoNothing().given(articleFileService).deleteArticleFile(anyLong());
        given(articleFileService.saveArticleFile(eq(multipartFile))).willReturn(Fixture.getArticleFile());
        willDoNothing().given(goodOptionService).deleteGoodOptions(eq(1L));
        given(goodOptionService.saveGoodOption(any(GoodOptionDto.class))).willReturn(goodOption);
        willDoNothing().given(dimensionService).deleteDimensions(eq(1L));
        willDoNothing().given(dimensionService).saveDimension(any(DimensionDto.class));
        willDoNothing().given(articleService).updateArticle(anyLong(), any(ArticleDto.class));
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
                                .param("goodOptions[0].optionName", "option1")
                                .param("goodOptions[0].printingTech", "123")
                                .param("goodOptions[0].material", "123")
                                .param("goodOptions[0].dimensions[0].dimName", "dimName")
                                .param("goodOptions[0].dimensions[0].dimValue", String.valueOf(100.0))
                                .param("goodOptions[0].dimensions[0].dimUnit", "MM")
                                .with(authentication(authentication))
                                .with(csrf())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/model_articles"))
                .andExpect(redirectedUrl("/model_articles"));

        // Then
        then(articleFileService).should().getArticleFile(anyLong());
        then(articleFileService).should().updateArticleFile(eq(multipartFile));
        then(articleFileService).should().deleteArticleFile(anyLong());
        then(articleFileService).should().saveArticleFile(eq(multipartFile));
        then(goodOptionService).should().deleteGoodOptions(eq(1L));
        then(goodOptionService).should().saveGoodOption(any(GoodOptionDto.class));
        then(dimensionService).should().deleteDimensions(eq(1L));
        then(dimensionService).should().saveDimension(any(DimensionDto.class));
        then(articleService).should().updateArticle(anyLong(), any(ArticleDto.class));
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
}
