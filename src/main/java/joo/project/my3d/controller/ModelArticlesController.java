package joo.project.my3d.controller;


import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.dto.ArticleDto;
import joo.project.my3d.dto.response.ArticleResponse;
import joo.project.my3d.dto.response.ArticleWithCommentsAndLikeCountResponse;
import joo.project.my3d.service.ArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/model_articles")
@RequiredArgsConstructor
public class ModelArticlesController {

    private final ArticleService articleService;

    @Value("${model.path}")
    private String modelPath;

    @GetMapping
    public String articles(
            @PageableDefault(size=9, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) ArticleCategory articleCategory,
            Model model
    ) {
        Page<ArticleDto> articles = articleService.getArticles(articleCategory, pageable);
        if (articles.isEmpty()) {
            model.addAttribute(
                    "articles",
                    List.of()
            );
        } else {
            model.addAttribute(
                    "articles",
                    articles
                            .filter(articleDto -> articleDto.articleType() == ArticleType.MODEL)
                            .map(ArticleResponse::from)
                            .stream().toList()
            );
        }

        model.addAttribute("modelPath", modelPath);

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
}
