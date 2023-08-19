package joo.project.my3d.controller;

import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.service.ArticleLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/like")
@RequiredArgsConstructor
public class ArticleLikeController {

    private final ArticleLikeService articleLikeService;

    @GetMapping("{articleId}")
    public String addArticleLike(
            @PathVariable Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        articleLikeService.addArticleLike(articleId, boardPrincipal.email());

        return "redirect:/model_articles/" + articleId;
    }

    @GetMapping("{articleId}/delete")
    public String deleteArticleLike(
            @PathVariable Long articleId
    ) {
        articleLikeService.deleteArticleLike(articleId);

        return "redirect:/model_articles/" + articleId;
    }
}
