package joo.project.my3d.controller;

import joo.project.my3d.dto.request.ArticleCommentRequest;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.service.ArticleCommentService;
import joo.project.my3d.service.ArticleLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

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
