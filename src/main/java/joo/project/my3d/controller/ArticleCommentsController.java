package joo.project.my3d.controller;

import joo.project.my3d.dto.request.ArticleCommentRequest;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.service.ArticleCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/comments")
@RequiredArgsConstructor
public class ArticleCommentsController {

    private final ArticleCommentService articleCommentService;

    @PostMapping("/new")
    public String postNewComment(
            ArticleCommentRequest articleCommentRequest,
            @RequestParam Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal) {

        articleCommentService.saveComment(articleCommentRequest.toDto(boardPrincipal.toDto()));

        return "redirect:/model_articles/" + articleId;
    }

    @PostMapping("{commentId}/delete")
    public String deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal) {

        articleCommentService.deleteComment(commentId, boardPrincipal.getUsername());

        return "redirect:/model_articles/" + articleId;
    }
}
