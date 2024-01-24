package joo.project.my3d.controller;

import com.querydsl.core.types.Predicate;
import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.dto.ArticleFileWithDimensionDto;
import joo.project.my3d.dto.ArticlePreviewDto;
import joo.project.my3d.dto.request.ArticleFormRequest;
import joo.project.my3d.dto.response.*;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.exception.ValidatedException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.service.ArticleFileService;
import joo.project.my3d.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static joo.project.my3d.domain.constant.FormStatus.*;

@Slf4j
@RestController
@RequestMapping("/model_articles")
@RequiredArgsConstructor
public class ModelArticlesController {

    private final ArticleService articleService;
    private final ArticleFileService articleFileService;

    /**
     * 게시판 페이지 요청
     */
    @GetMapping
    public ResponseEntity<PagedResponse<ArticlePreviewDto>> articles(
            @PageableDefault(size=9, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @QuerydslPredicate(root = Article.class) Predicate predicate
    ) {

        //파일 경로, 카테고리 리스트, 페이지네이션 바 숫자 리스트는 프론트엔드에서 처리
        return ResponseEntity.ok(
                PagedResponse.fromArticlePreview(
                        articleService.getArticlesForPreview(predicate, pageable)
                )
        );
    }

    /**
     * 특정 게시글 페이지 요청
     */
    @GetMapping("/{articleId}")
    public ResponseEntity<ArticleDetailResponse> article(
            @PathVariable Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {

        return ResponseEntity.ok(
                articleService.getArticleWithComments(articleId, boardPrincipal.email())
        );
    }

    /**
     * 게시글 작성 페이지 요청 (프론트엔드 작업시 불필요하면 삭제)
     */
    @GetMapping("/add")
    public ResponseEntity<ArticleFormResponse> articleAddForm() {
        return ResponseEntity.ok(ArticleFormResponse.of(CREATE));
    }

    /**
     * 게시글 저장 요청
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> postNewArticle(
            @ModelAttribute("article") @Validated ArticleFormRequest articleFormRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        if (bindingResult.hasErrors()) {
            log.warn("bindingResult={}", bindingResult);
            throw new ValidatedException(
                    ErrorCode.INVALID_REQUEST,
                    ExceptionResponse.fromBindingResult(
                            "validation error during add article",
                            bindingResult
                    )
            );
        }

        //게시글 저장
        ArticleFileWithDimensionDto articleFile = articleFormRequest.toArticleFileWithDimensionDto();
        articleService.saveArticle(
                boardPrincipal.email(),
                articleFormRequest.toArticleDto(
                        articleFile,
                        boardPrincipal.toDto(),
                        ArticleType.MODEL
                ),
                articleFormRequest.getModelFile()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("You successfully added article"));
    }

    /**
     * 특정 게시글 수정을 위해 요청 게시글의 기존 데이터 요청
     */
    @GetMapping("/update/{articleId}")
    public ResponseEntity<ArticleFormResponse> articleUpdateForm(@PathVariable Long articleId) {

        return ResponseEntity.ok(
                ArticleFormResponse.from(
                        articleService.getArticleForm(articleId),
                        UPDATE
                )
        );
    }

    /**
     * 특정 게시글 수정 요청
     */
    @PutMapping("/update/{articleId}")
    public ResponseEntity<ApiResponse> postUpdateArticle(
            @PathVariable Long articleId,
            @ModelAttribute("article") @Validated ArticleFormRequest articleFormRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        if (bindingResult.hasErrors()) {
            log.warn("formBindingResult={}", bindingResult);
            throw new ValidatedException(
                    ErrorCode.INVALID_REQUEST,
                    ExceptionResponse.fromBindingResult(
                            "validation error during updated article",
                            bindingResult
                    )
            );
        }

        articleService.updateArticle(
                articleFormRequest,
                articleId,
                boardPrincipal.email()
        );

        return ResponseEntity.ok(
                ApiResponse.of("You successfully updated article")
        );
    }

    /**
     * 특정 게시글 삭제 요청
     */
    @DeleteMapping("/{articleId}")
    public ResponseEntity<ApiResponse> deleteArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        articleService.deleteArticle(articleId, boardPrincipal.email());

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ApiResponse.of("You successfully deleted article"));
    }

    /**
     * 특정 게시글의 모델 파일 다운로드 요청
     */
    @GetMapping("/download/{articleId}")
    public ResponseEntity<byte[]> downloadArticleFile(@PathVariable Long articleId) {
        byte[] file = articleFileService.download(articleId);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentLength(file.length);
        httpHeaders.setContentDispositionFormData("attachment", "");
        return ResponseEntity.status(HttpStatus.OK)
                .headers(httpHeaders)
                .body(file);
    }
}
