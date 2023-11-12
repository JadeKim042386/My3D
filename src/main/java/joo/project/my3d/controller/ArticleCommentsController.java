package joo.project.my3d.controller;

import joo.project.my3d.dto.request.ArticleCommentRequest;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.service.ArticleCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
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

        try {
            articleCommentService.saveComment(articleCommentRequest.toDto(boardPrincipal.toDto()));
        } catch (RuntimeException e) {
            log.error("댓글 저장/추가 실패 - {}", e);
        }

        return "redirect:/model_articles/" + articleId;
    }

    @PostMapping("{commentId}/delete")
    public String deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal) {

        try{
            articleCommentService.deleteComment(commentId, boardPrincipal.getUsername());
        } catch (RuntimeException e) {
            log.error("댓글 삭제 실패 - {}", e);
        }

        return "redirect:/model_articles/" + articleId;
    }
}
