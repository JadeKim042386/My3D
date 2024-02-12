package joo.project.my3d.api;

import joo.project.my3d.domain.Article;
import joo.project.my3d.dto.request.ArticleFormRequest;
import joo.project.my3d.dto.response.ApiResponse;
import joo.project.my3d.dto.response.ExceptionResponse;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.exception.ValidatedException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.service.ArticleFileServiceInterface;
import joo.project.my3d.service.ArticleServiceInterface;
import joo.project.my3d.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/v1/articles")
@RequiredArgsConstructor
public class ArticlesApi {

    private final ArticleServiceInterface articleService;
    private final ArticleFileServiceInterface articleFileService;

    /**
     * 게시글 저장 요청
     */
    @PostMapping
    public ResponseEntity<ApiResponse> postNewArticle(
            @ModelAttribute("article") @Valid ArticleFormRequest articleFormRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal) {
        if (bindingResult.hasErrors()) {
            log.warn("bindingResult={}", bindingResult);
            throw new ValidatedException(
                    ErrorCode.INVALID_REQUEST,
                    ExceptionResponse.fromBindingResult("validation error during add article", bindingResult));
        }

        // 게시글 저장
        Article article = articleService.saveArticle(boardPrincipal.email(), articleFormRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(String.valueOf(article.getId())));
    }

    /**
     * 특정 게시글 수정 요청
     */
    @PutMapping("/{articleId}")
    public ResponseEntity<ApiResponse> postUpdateArticle(
            @PathVariable Long articleId,
            @ModelAttribute("article") @Valid ArticleFormRequest articleFormRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal) {
        if (bindingResult.hasErrors()) {
            log.warn("formBindingResult={}", bindingResult);
            throw new ValidatedException(
                    ErrorCode.INVALID_REQUEST,
                    ExceptionResponse.fromBindingResult("validation error during updated article", bindingResult));
        }

        articleService.updateArticle(articleFormRequest, articleId, boardPrincipal.email());

        return ResponseEntity.ok(ApiResponse.of(String.valueOf(articleId)));
    }

    /**
     * 특정 게시글 삭제 요청
     */
    @DeleteMapping("/{articleId}")
    public ResponseEntity<ApiResponse> deleteArticle(
            @PathVariable Long articleId, @AuthenticationPrincipal BoardPrincipal boardPrincipal) {
        articleService.deleteArticle(articleId, boardPrincipal.email());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.of("You successfully deleted article"));
    }

    /**
     * 특정 게시글의 모델 파일 다운로드 요청
     */
    @GetMapping("/{articleId}/download")
    public ResponseEntity<byte[]> downloadArticleFile(@PathVariable Long articleId) {
        String fileName = articleFileService.searchFileName(articleId);
        byte[] file = articleFileService.download(fileName);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentLength(file.length);
        httpHeaders.setContentDispositionFormData("attachment", "model." + FileUtils.getExtension(fileName));
        return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body(file);
    }
}
