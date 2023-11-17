package joo.project.my3d.controller;

import com.querydsl.core.types.Predicate;
import joo.project.my3d.domain.*;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.domain.constant.FormStatus;
import joo.project.my3d.dto.ArticleDto;
import joo.project.my3d.dto.ArticleFileDto;
import joo.project.my3d.dto.DimensionDto;
import joo.project.my3d.dto.DimensionOptionDto;
import joo.project.my3d.dto.request.ArticleFormRequest;
import joo.project.my3d.dto.request.DimensionOptionRequest;
import joo.project.my3d.dto.response.*;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.repository.ArticleLikeRepository;
import joo.project.my3d.service.*;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/model_articles")
@RequiredArgsConstructor
public class ModelArticlesController {

    private final ArticleService articleService;
    private final PaginationService paginationService;
    private final ArticleFileService articleFileService;
    private final ArticleLikeRepository articleLikeRepository;
    private final AlarmService alarmService;
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
        Page<ArticleDto> articles = articleService.getArticles(predicate, pageable);
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
            @RequestParam(required = false) Long alarmId,
            Model model,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        Optional<ArticleLike> articleLike = articleLikeRepository.findByUserAccount_EmailAndArticle_Id(boardPrincipal.email(), articleId);
        ArticleWithCommentsAndLikeCountResponse article = ArticleWithCommentsAndLikeCountResponse.from(articleService.getArticleWithComments(articleId));

        model.addAttribute("article", article);
        model.addAttribute("articleComments", article.articleCommentResponses());
        model.addAttribute("modelFile", article.modelFile());
        model.addAttribute("addedLike", articleLike.isPresent());
        model.addAttribute("modelPath", S3Url);

        if (alarmId != null) {
            alarmService.checkAlarm(alarmId);
        }

        return "model_articles/detail";
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
            model.addAttribute("formStatus", FormStatus.CREATE);
            model.addAttribute("categories", ArticleCategory.values());
            return "/model_articles/form";
        }

        try {
            //파일과 치수 저장
            ArticleFile articleFile = articleFileService.saveArticleFileWithForm(articleFormRequest);

            //게시글 저장
            articleService.saveArticle(
                    articleFormRequest.toArticleDto(
                            ArticleFileDto.from(articleFile),
                            boardPrincipal.toDto(),
                            ArticleType.MODEL
                    )
            );
        } catch (RuntimeException e) {
            log.error("게시글 추가 실패 - {}", e);
            model.addAttribute("formStatus", FormStatus.CREATE);
            model.addAttribute("categories", ArticleCategory.values());
            return "/model_articles/form";
        }

        return "redirect:/model_articles";
    }

    @GetMapping("/form/{articleId}")
    public String updateArticle(
            @PathVariable Long articleId,
            Model model
    ) {
        model.addAttribute(
            "article",
            ArticleFormResponse.from(
                articleService.getArticle(articleId),
                articleFileService.getArticleFile(articleId)
            )
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
            model.addAttribute("formStatus", FormStatus.UPDATE);
            model.addAttribute("categories", ArticleCategory.values());
            return "model_articles/form";
        }
        try {
            articleFileService.updateArticleFile(articleFormRequest, articleId);

            articleService.updateArticle(
                    articleId,
                    articleFormRequest.toArticleDto(
                            boardPrincipal.toDto()
                    )
            );
        } catch (RuntimeException e) {
            log.error("게시글 수정 실패 - {}", e);
            model.addAttribute("formStatus", FormStatus.UPDATE);
            model.addAttribute("categories", ArticleCategory.values());
            return "model_articles/form";
        }

        return "redirect:/model_articles";
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
        } catch (RuntimeException e) {
            log.error("게시글 삭제 실패 - {}", e);
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
