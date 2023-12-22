package joo.project.my3d.controller;

import io.swagger.v3.oas.annotations.Operation;
import joo.project.my3d.dto.response.ArticleLikeResponse;
import joo.project.my3d.dto.response.Response;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.exception.ArticleLikeException;
import joo.project.my3d.service.ArticleLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class ArticleLikeController {

    private final ArticleLikeService articleLikeService;

    @Operation(summary = "특정 게시글에 좋아요 추가")
    @GetMapping("/{articleId}")
    public Response<ArticleLikeResponse> addArticleLike(
            @PathVariable Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        try{
            int likeCount = articleLikeService.addArticleLike(articleId, boardPrincipal.email());
            return Response.success(ArticleLikeResponse.of(likeCount));
        } catch (ArticleLikeException e) {
            log.error("게시글 좋아요 추가 실패 - {}", e.getMessage());
            throw e;
        }
    }

    @Operation(summary = "특정 게시글의 좋아요 해제")
    @GetMapping("/{articleId}/delete")
    public Response<ArticleLikeResponse> deleteArticleLike(
            @PathVariable Long articleId
    ) {
        int likeCount = articleLikeService.deleteArticleLike(articleId);

        return Response.success(ArticleLikeResponse.of(likeCount));
    }
}
