package joo.project.my3d.controller;

import joo.project.my3d.dto.response.ArticleLikeResponse;
import joo.project.my3d.dto.response.Response;
import joo.project.my3d.dto.security.BoardPrincipal;
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

    @GetMapping("/{articleId}")
    public Response<ArticleLikeResponse> addArticleLike(
            @PathVariable Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        try{
            int likeCount = articleLikeService.addArticleLike(articleId, boardPrincipal.email());
            return Response.success(ArticleLikeResponse.of(likeCount));
        } catch (RuntimeException e) {
            log.error("게시글 좋아요 추가 실패 - {}", e);
            return Response.error(e.getMessage());
        }
    }

    @GetMapping("/{articleId}/delete")
    public Response<ArticleLikeResponse> deleteArticleLike(
            @PathVariable Long articleId
    ) {
        int likeCount = articleLikeService.deleteArticleLike(articleId);

        return Response.success(ArticleLikeResponse.of(likeCount));
    }
}
