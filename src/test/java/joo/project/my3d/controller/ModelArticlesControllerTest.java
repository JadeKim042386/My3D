package joo.project.my3d.controller;

import com.querydsl.core.types.Predicate;
import joo.project.my3d.config.TestSecurityConfig;
import joo.project.my3d.domain.ArticleLike;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.domain.constant.UserRole;
import joo.project.my3d.dto.ArticleDto;
import joo.project.my3d.dto.ArticleFileDto;
import joo.project.my3d.dto.ArticleWithCommentsAndLikeCountDto;
import joo.project.my3d.dto.UserAccountDto;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.repository.ArticleLikeRepository;
import joo.project.my3d.service.ArticleFileService;
import joo.project.my3d.service.ArticleService;
import joo.project.my3d.service.PaginationService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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

    @Disabled("구현 중...")
    @DisplayName("[POST] 게시글 추가 - 정상")
    @Test
    void addNewModelArticle() throws Exception {
        // Given
        byte[] file = Fixture.getMultipartFile().getBytes();
        given(articleFileService.saveArticleFile(any(MultipartFile.class))).willReturn(any(ArticleFileDto.class));
        willDoNothing().given(articleService).saveArticle(any(ArticleDto.class));
        // When
        mvc.perform(
                        multipart("/model_articles/form")
                                .file("file", file)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "title")
                                .param("content", "content")
                                .param("articleCategory", "MUSIC")
                                .with(user("jooCompany").password("pw").roles("COMPANY"))
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/model_articles"))
                .andExpect(redirectedUrl("/model_articles"));

        // Then
        then(articleFileService).should().saveArticleFile(any(MultipartFile.class));
        then(articleService).should().saveArticle(any(ArticleDto.class));
    }

    @Disabled("구현 중...")
    @DisplayName("[POST] 게시글 추가 - 파일 누락")
    @Test
    void addNewModelArticleWithoutFile() throws Exception {
        // Given

        // When
        mvc.perform(
                        multipart("/model_articles/form")
                                .file("modelFile", null)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "title")
                                .param("content", "content")
                                .param("articleCategory", "MUSIC")
                                .with(user("jooCompany").password("pw").roles("COMPANY"))
                )
                .andExpect(status().isOk())
                .andExpect(view().name("model_articles/form"));

        // Then
    }

    @Disabled("구현 중...")
    @DisplayName("[POST] 게시글 추가 - 카테고리 누락")
    @Test
    void addNewModelArticleWithoutCategory() throws Exception {
        // Given
        byte[] file = Fixture.getMultipartFile().getBytes();
        // When
        mvc.perform(
                        multipart("/model_articles/form")
                                .file("file", file)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "title")
                                .param("content", "content")
                                .param("articleCategory", "카테고리를 선택해주세요.")
                                .with(user("jooCompany").password("pw").roles("COMPANY"))
                )
                .andExpect(status().isOk())
                .andExpect(view().name("model_articles/form"));

        // Then
    }

    @Disabled("구현 중...")
    @DisplayName("[POST] 게시글 추가 - 제목 누락")
    @Test
    void addNewModelArticleWithoutTitle() throws Exception {
        // Given
        byte[] file = Fixture.getMultipartFile().getBytes();
        // When
        mvc.perform(
                        multipart("/model_articles/form")
                                .file("file", file)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "")
                                .param("content", "content")
                                .param("articleCategory", "MUSIC")
                                .with(user("jooCompany").password("pw").roles("COMPANY"))
                )
                .andExpect(status().isOk())
                .andExpect(view().name("model_articles/form"));

        // Then
    }

    @Disabled("구현 중...")
    @DisplayName("[POST] 게시글 추가 - 본문 누락")
    @Test
    void addNewModelArticleWithoutContent() throws Exception {
        // Given
        byte[] file = Fixture.getMultipartFile().getBytes();
        // When
        mvc.perform(
                        multipart("/model_articles/form")
                                .file("modelFile", file)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "title")
                                .param("content", "")
                                .param("articleCategory", "MUSIC")
                                .with(user("jooCompany").password("pw").roles("COMPANY"))
                )
                .andExpect(status().isOk())
                .andExpect(view().name("model_articles/form"));

        // Then
    }

    @DisplayName("[GET] 게시글 페이지")
    @Test
    void modelArticle() throws Exception {
        // Given
        Long articleId = 1L;
        ArticleWithCommentsAndLikeCountDto dto = FixtureDto.getArticleWithCommentsAndLikeCountDto("title", "content", ArticleType.MODEL, ArticleCategory.ARCHITECTURE);
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
                .andExpect(model().attributeExists("articleFiles"))
                .andExpect(model().attributeExists("addedLike"));

        // Then
        then(articleLikeRepository).should().findByUserAccount_EmailAndArticle_Id(articleLike.getUserAccount().getEmail(), articleId);
        then(articleService).should().getArticleWithComments(articleId);
    }

    @DisplayName("[GET] 게시글 수정 페이지")
    @Test
    void updateModelArticle() throws Exception {
        // Given
        ArticleDto articleDto = FixtureDto.getArticleDto(1L, "title", "summary", "content", ArticleType.MODEL, ArticleCategory.MUSIC);
        given(articleService.getArticle(anyLong())).willReturn(articleDto);
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
        byte[] file = Fixture.getMultipartFile().getBytes();
        given(articleService.getArticleFiles(anyLong())).willReturn(List.of());
        given(articleFileService.updateArticleFile(anyList(), anyList())).willReturn(true);
        willDoNothing().given(articleService).updateArticle(anyLong(), any(ArticleDto.class));
        UsernamePasswordAuthenticationToken authentication = FixtureDto.getAuthentication("jooCompany", UserRole.COMPANY);
        // When
        mvc.perform(
                        multipart("/model_articles/form/1")
                                .file("modelFile", file)
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                                .param("title", "title")
                                .param("summary", "summary")
                                .param("content", "content")
                                .param("priceValue", String.valueOf(10000))
                                .param("deliveryPrice", String.valueOf(3000))
                                .param("articleCategory", "MUSIC")
                                .with(authentication(authentication))
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/model_articles"))
                .andExpect(redirectedUrl("/model_articles"));

        // Then
        then(articleService).should().getArticleFiles(anyLong());
        then(articleFileService).should().updateArticleFile(anyList(), anyList());
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
