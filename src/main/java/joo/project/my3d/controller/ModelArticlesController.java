package joo.project.my3d.controller;

import com.querydsl.core.types.Predicate;
import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.domain.constant.FormStatus;
import joo.project.my3d.dto.ArticleDto;
import joo.project.my3d.dto.request.ArticleRequest;
import joo.project.my3d.dto.response.ArticleResponse;
import joo.project.my3d.dto.response.ArticleWithCommentsAndLikeCountResponse;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.service.ArticleService;
import joo.project.my3d.service.PaginationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/model_articles")
@RequiredArgsConstructor
public class ModelArticlesController {

    private final ArticleService articleService;
    private final PaginationService paginationService;

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
        ArticleWithCommentsAndLikeCountResponse article = ArticleWithCommentsAndLikeCountResponse.from(articleService.getArticle(articleId));

        model.addAttribute("article", article);
        model.addAttribute("articleComments", article.articleCommentResponses());
        model.addAttribute("articleFile", article.articleFileResponse());

        return "model_articles/detail";
    }

    @GetMapping("/form")
    public String articleForm(Model model) {
        model.addAttribute("formStatus", FormStatus.CREATE);
        model.addAttribute("categories", ArticleCategory.values());
        return "model_articles/form";
    }

    @PostMapping("/form")
    public String postNewArticle(
            ArticleRequest articleRequest,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {

        articleService.saveArticle(
                articleRequest.toDto(
                        boardPrincipal.toDto(),
                        ArticleType.MODEL,
                        0
                )
        );
        return "redirect:/model_articles";
    }
}
