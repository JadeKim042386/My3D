package joo.project.my3d.controller;

import joo.project.my3d.dto.request.ArticleCommentRequest;
import joo.project.my3d.dto.response.ApiResponse;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.service.ArticleCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class ArticleCommentsController {

    private final ArticleCommentService articleCommentService;

    /**
     *  댓글 추가
     */
    @PostMapping
    public ApiResponse<Void> postNewComment(
            ArticleCommentRequest articleCommentRequest,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        articleCommentService.saveComment(
                articleCommentRequest.toDto(
                        boardPrincipal.nickname(),
                        boardPrincipal.email()
                )
        );

        return ApiResponse.success();
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        articleCommentService.deleteComment(commentId, boardPrincipal.getUsername());

        return ApiResponse.success();
    }
}
