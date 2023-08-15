package joo.project.my3d.controller;

import com.querydsl.core.types.Predicate;
import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.domain.constant.FormStatus;
import joo.project.my3d.dto.ArticleDto;
import joo.project.my3d.dto.ArticleFileDto;
import joo.project.my3d.dto.request.ArticleFormRequest;
import joo.project.my3d.dto.response.ArticleFormResponse;
import joo.project.my3d.dto.response.ArticleResponse;
import joo.project.my3d.dto.response.ArticleWithCommentsAndLikeCountResponse;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.service.ArticleFileService;
import joo.project.my3d.service.ArticleService;
import joo.project.my3d.service.PaginationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/model_articles")
@RequiredArgsConstructor
public class ModelArticlesController {

    private final ArticleService articleService;
    private final PaginationService paginationService;
    private final ArticleFileService articleFileService;

    @Value("${model.path}")
    private String modelPath;

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

        model.addAttribute("modelPath", modelPath);
        model.addAttribute("categories", ArticleCategory.values());
        model.addAttribute("paginationBarNumbers", barNumbers);

        return "model_articles/index";
    }

    @GetMapping("/{articleId}")
    public String article(
            @PathVariable Long articleId,
            Model model
    ) {
        ArticleWithCommentsAndLikeCountResponse article = ArticleWithCommentsAndLikeCountResponse.from(articleService.getArticleWithComments(articleId));

        model.addAttribute("article", article);
        model.addAttribute("articleComments", article.articleCommentResponses());
        model.addAttribute("articleFile", article.articleFileResponse());

        return "model_articles/detail";
    }

    @GetMapping("/form")
    public String articleForm(Model model) {
        model.addAttribute("article", new ArticleFormResponse(null, null, null, null, null));
        model.addAttribute("formStatus", FormStatus.CREATE);
        model.addAttribute("categories", ArticleCategory.values());
        return "model_articles/form";
    }

    @PostMapping("/form")
    public String postNewArticle(
            @Validated @ModelAttribute("article") ArticleFormRequest articleFormRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            log.warn("bindingResult={}", bindingResult);
            model.addAttribute("formStatus", FormStatus.CREATE);
            model.addAttribute("categories", ArticleCategory.values());
            return "model_articles/form";
        }

        String savedFileName = articleFileService.saveArticleFile(articleFormRequest.file());
        articleService.saveArticle(
                articleFormRequest.toDto(
                        boardPrincipal.toDto(),
                        ArticleType.MODEL,
                        savedFileName
                )
        );
        return "redirect:/model_articles";
    }

    @GetMapping("/form/{articleId}")
    public String updateArticle(
            @PathVariable Long articleId,
            Model model
    ) {
        model.addAttribute("article", ArticleFormResponse.from(articleService.getArticle(articleId)));
        model.addAttribute("formStatus", FormStatus.UPDATE);
        model.addAttribute("categories", ArticleCategory.values());
        return "model_articles/form";
    }

    @PostMapping("/form/{articleId}")
    public String postUpdateArticle(
            @PathVariable Long articleId,
            @Validated @ModelAttribute("article") ArticleFormRequest articleFormRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            log.warn("bindingResult={}", bindingResult);
            model.addAttribute("formStatus", FormStatus.UPDATE);
            model.addAttribute("categories", ArticleCategory.values());
            return "model_articles/form";
        }

        ArticleFileDto articleFile = articleService.getArticleFile(articleId); //저장되어있는 파일
        boolean isUpdated = articleFileService.updateArticleFile(articleFormRequest.file(), articleFile);
        articleService.updateArticle(
                articleId,
                articleFormRequest.toDto(boardPrincipal.toDto(), articleFile.fileName(), isUpdated)
        );
        return "redirect:/model_articles";
    }

    @PostMapping("{articleId}/delete")
    public String deleteArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        articleService.deleteArticle(articleId, boardPrincipal.username());

        return "redirect:/model_articles";
    }
}
