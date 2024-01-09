package joo.project.my3d.controller;

import com.querydsl.core.types.Predicate;
import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.domain.constant.FormStatus;
import joo.project.my3d.dto.*;
import joo.project.my3d.dto.request.ArticleFormRequest;
import joo.project.my3d.dto.response.ArticleFormResponse;
import joo.project.my3d.dto.response.ArticleResponse;
import joo.project.my3d.dto.response.ArticleWithCommentsAndLikeCountResponse;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.exception.ArticleException;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Controller
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
    public String articles(
            @PageableDefault(size=9, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @QuerydslPredicate(root = Article.class) Predicate predicate,
            Model model
    ) {
        Page<ArticlesDto> articles = articleService.getArticles(predicate, pageable);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());
        if (articles.isEmpty()) {
            model.addAttribute(
                "articles",
                Page.empty()
            );
        } else {
            model.addAttribute(
                "articles",
                new PageImpl<>(
                    articles
                        .filter(articleDto -> articleDto.articleType() == ArticleType.MODEL)
                        .map(ArticleResponse::from)
                        .toList(),
                    pageable,
                    articles.getTotalElements()
                )
            );
        }

        model.addAttribute("modelPath", S3Url);
        model.addAttribute("categories", ArticleCategory.values());
        model.addAttribute("paginationBarNumbers", barNumbers);

        return "model_articles/index";
    }

    /**
     * 게시글 페이지 요청
     */
    @GetMapping("/{articleId}")
    public String article(
            @PathVariable Long articleId,
            Model model,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        try {
            int likeCount = articleLikeRepository.countByUserAccount_EmailAndArticle_Id(boardPrincipal.email(), articleId);
            ArticleWithCommentsAndLikeCountDto articleWithComments = articleService.getArticleWithComments(articleId);
            ArticleWithCommentsAndLikeCountResponse article = ArticleWithCommentsAndLikeCountResponse.from(articleWithComments);

            model.addAttribute("article", article);
            model.addAttribute("articleComments", article.articleCommentResponses());
            model.addAttribute("modelFile", article.modelFile());
            model.addAttribute("addedLike", likeCount > 0);
            model.addAttribute("modelPath", S3Url);

            return "model_articles/detail";
        } catch (ArticleException e) {
            log.error("게시글을 찾을 수 없습니다. id: {} - {}", articleId, e.getMessage());
            return "model_articles/index";
        }
    }

    @GetMapping("/form")
    public String articleForm(Model model) {
        model.addAttribute("article", ArticleFormResponse.of());
        model.addAttribute("formStatus", FormStatus.CREATE);
        model.addAttribute("categories", ArticleCategory.values());
        return "model_articles/form";
    }

    /**
     * 게시글 저장 요청
     */
    @PostMapping("/form")
    public String postNewArticle(
            @ModelAttribute("article") @Validated ArticleFormRequest articleFormRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            log.warn("bindingResult={}", bindingResult);
            FieldError dimensionOptionsError = bindingResult.getFieldError("dimensionOptions");
            if (Objects.nonNull(dimensionOptionsError)) {
                model.addAttribute("dimensionOptionError", dimensionOptionsError.getDefaultMessage());
            }
            return articleSaveFailed(articleFormRequest, model);
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
            log.error("Amazon S3에 파일 저장 실패 - {}", new FileException(ErrorCode.FILE_CANT_SAVE, e).getMessage());
            return articleSaveFailed(articleFormRequest, model);
        }

        return "redirect:/model_articles";
    }

    /**
     * 게시글 추가에 실패했을때 입력한 데이터를 다시 보내주기위한 메소드
     */
    private static String articleSaveFailed(ArticleFormRequest articleFormRequest, Model model) {
        model.addAttribute("article", ArticleFormResponse.from(articleFormRequest));
        model.addAttribute("formStatus", FormStatus.CREATE);
        model.addAttribute("categories", ArticleCategory.values());
        return "/model_articles/form";
    }

    @GetMapping("/form/{articleId}")
    public String updateArticle(
            @PathVariable Long articleId,
            Model model
    ) {
        ArticleFormDto article = articleService.getArticle(articleId);
        model.addAttribute(
            "article",
            ArticleFormResponse.from(article)
        );
        model.addAttribute("formStatus", FormStatus.UPDATE);
        model.addAttribute("categories", ArticleCategory.values());
        return "model_articles/form";
    }

    /**
     * 게시글 수정 요청
     */
    @PostMapping("/form/{articleId}")
    public String postUpdateArticle(
            @PathVariable Long articleId,
            @ModelAttribute("article") @Validated ArticleFormRequest articleFormRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            log.warn("formBindingResult={}", bindingResult);
            FieldError dimensionOptionsError = bindingResult.getFieldError("dimensionOptions");
            if (Objects.nonNull(dimensionOptionsError)) {
                model.addAttribute("dimensionOptionError", dimensionOptionsError.getDefaultMessage());
            }
            return articleUpdateFailed(articleId, articleFormRequest, model);
        }
        try {
            articleFileService.updateArticleFile(articleFormRequest, articleId);

            articleService.updateArticle(
                    articleId,
                    articleFormRequest.toArticleDto(),
                    boardPrincipal.email()
            );
        } catch (FileException | ArticleException e) {
            log.error("게시글 수정 실패 - {}", e.getMessage());
            return articleUpdateFailed(articleId, articleFormRequest, model);
        }

        return "redirect:/model_articles/" + articleId;
    }

    private static String articleUpdateFailed(Long articleId, ArticleFormRequest articleFormRequest, Model model) {
        model.addAttribute("article", ArticleFormResponse.from(articleId, articleFormRequest));
        model.addAttribute("formStatus", FormStatus.UPDATE);
        model.addAttribute("categories", ArticleCategory.values());
        return "model_articles/form";
    }

    /**
     * 게시글 삭제 요청
     */
    @PostMapping("{articleId}/delete")
    public String deleteArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        try {
            articleFileService.deleteArticleFile(articleId);
            articleService.deleteArticle(articleId, boardPrincipal.email());
        } catch (ArticleException e) {
            log.error("게시글 삭제 실패 - {}", e.getMessage());
            return "redirect:/model_articles/" + articleId;
        } catch (FileException e) {
            log.error("파일 삭제 실패 - {}", e.getMessage());
            return "redirect:/model_articles/" + articleId;
        }

        return "redirect:/model_articles";
    }

    @GetMapping("{articleId}/download")
    public ResponseEntity<byte[]> downloadArticleFile(
            @PathVariable Long articleId
    ) {
        ArticleFileDto articleFile = articleFileService.getArticleFile(articleId);
        byte[] downloadFile = s3Service.downloadFile(articleFile.fileName());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentLength(downloadFile.length);
        httpHeaders.setContentDispositionFormData("attachment", articleFile.originalFileName());

        return new ResponseEntity<>(downloadFile, httpHeaders, HttpStatus.OK);
    }
}
