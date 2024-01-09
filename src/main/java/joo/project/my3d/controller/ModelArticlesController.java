package joo.project.my3d.controller;

import com.querydsl.core.types.Predicate;
import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.dto.*;
import joo.project.my3d.dto.request.ArticleFormRequest;
import joo.project.my3d.dto.response.ApiResponse;
import joo.project.my3d.dto.response.ArticleDetailResponse;
import joo.project.my3d.dto.response.ArticleFormResponse;
import joo.project.my3d.dto.response.ArticlePreviewResponse;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.exception.FileException;
import joo.project.my3d.repository.ArticleLikeRepository;
import joo.project.my3d.service.ArticleFileService;
import joo.project.my3d.service.ArticleService;
import joo.project.my3d.service.PaginationService;
import joo.project.my3d.service.aws.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static joo.project.my3d.domain.constant.FormStatus.CREATE;
import static joo.project.my3d.domain.constant.FormStatus.UPDATE;

@Slf4j
@RestController
@RequestMapping("/model_articles")
@RequiredArgsConstructor
public class ModelArticlesController {

    private final ArticleService articleService;
    private final PaginationService paginationService;
    private final ArticleFileService articleFileService;
    private final ArticleLikeRepository articleLikeRepository;
    private final S3Service s3Service;

    @Value("${aws.s3.url}")
    private String S3Url;

    /**
     * 게시판 페이지 요청
     */
    @GetMapping
    public ApiResponse<ArticlePreviewResponse> articles(
            @PageableDefault(size=9, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @QuerydslPredicate(root = Article.class) Predicate predicate
    ) {
        Page<ArticlePreviewDto> articles = articleService.getArticlesForPreview(predicate, pageable);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());
        if (!articles.isEmpty()) {
            articles = new PageImpl<>(articles
                    .filter(articleDto -> articleDto.articleType() == ArticleType.MODEL)
                    .toList(),
                    pageable,
                    articles.getTotalElements()
            );
        }

        return ApiResponse.success(ArticlePreviewResponse.of(
                articles,
                S3Url,
                ArticleCategory.values(),
                barNumbers
        ));
    }

    /**
     * 특정 게시글 페이지 요청
     */
    @GetMapping("/{articleId}")
    public ApiResponse<ArticleDetailResponse> article(
            @PathVariable Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        int likeCount = articleLikeRepository.countByUserAccount_EmailAndArticle_Id(boardPrincipal.email(), articleId);
        ArticleWithCommentsAndLikeCountDto article = articleService.getArticleWithComments(articleId);

        return ApiResponse.success(ArticleDetailResponse.of(article, likeCount > 0, S3Url));
    }

    /**
     * 게시글 추가를 위한 기본 폼 요청 (프론트엔드 작업시 불필요하면 삭제)
     */
    @GetMapping("/form")
    public ApiResponse<ArticleFormResponse> articleAddForm() {
        return ApiResponse.success(ArticleFormResponse.of(CREATE));
    }

    /**
     * 게시글 저장 요청
     */
    @PostMapping("/form")
    public ApiResponse<?> postNewArticle(
            @ModelAttribute("article") @Validated ArticleFormRequest articleFormRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        if (bindingResult.hasErrors()) {
            log.warn("bindingResult={}", bindingResult);
            return ApiResponse.invalid(ArticleFormResponse.validError(
                    articleFormRequest,
                    CREATE,
                    bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).toList()
            ));
        }

        try {
            ArticleFileWithDimensionOptionWithDimensionDto articleFile = articleFormRequest.toArticleFileWithDimensionDto();

            //게시글 저장
            articleService.saveArticle(
                    boardPrincipal.email(),
                    articleFormRequest.toArticleDto(
                            articleFile,
                            boardPrincipal.toDto(),
                            ArticleType.MODEL
                    )
            );

            //S3 파일 저장
            s3Service.uploadFile(articleFormRequest.getModelFile(), articleFile.fileName());
        } catch (IOException e) {
            log.error("Amazon S3에 파일 저장 실패");
            throw new FileException(ErrorCode.FILE_CANT_SAVE, e);
        }

        return ApiResponse.success();
    }

    /**
     * 특정 게시글 수정을 위해 요청 게시글의 기존 데이터 요청
     */
    @GetMapping("/form/{articleId}")
    public ApiResponse<?> articleUpdateForm(@PathVariable Long articleId) {
        ArticleFormDto article = articleService.getArticleForm(articleId);

        return ApiResponse.success(ArticleFormResponse.from(article, UPDATE));
    }

    /**
     * 특정 게시글 수정 요청
     */
    @PostMapping("/form/{articleId}")
    public ApiResponse<?> postUpdateArticle(
            @PathVariable Long articleId,
            @ModelAttribute("article") @Validated ArticleFormRequest articleFormRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        if (bindingResult.hasErrors()) {
            log.warn("formBindingResult={}", bindingResult);
            return ApiResponse.invalid(ArticleFormResponse.validError(
                    articleId,
                    articleFormRequest,
                    UPDATE,
                    bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).toList()
            ));
        }

        articleFileService.updateArticleFile(articleFormRequest, articleId);
        articleService.updateArticle(
                articleId,
                articleFormRequest.toArticleDto(),
                boardPrincipal.email()
        );

        return ApiResponse.success();
    }

    /**
     * 특정 게시글 삭제 요청
     */
    @PostMapping("{articleId}/delete")
    public ApiResponse<Void> deleteArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        articleFileService.deleteArticleFile(articleId);
        articleService.deleteArticle(articleId, boardPrincipal.email());

        return ApiResponse.success();
    }

    /**
     * 특정 게시글의 모델 파일 다운로드 요청
     */
    @GetMapping("{articleId}/download")
    public ApiResponse<byte[]> downloadArticleFile(@PathVariable Long articleId) {
        ArticleFileDto articleFile = articleFileService.getArticleFile(articleId);
        byte[] downloadFile = s3Service.downloadFile(articleFile.fileName());

        return ApiResponse.success(downloadFile);
    }
}
