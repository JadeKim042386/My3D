package joo.project.my3d.controller;

import joo.project.my3d.dto.response.ApiResponse;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.service.ArticleLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class ArticleLikeController {

    private final ArticleLikeService articleLikeService;

    /**
     * 특정 게시글에 좋아요 추가
     */
    @PostMapping("/{articleId}")
    public ApiResponse<Integer> addArticleLike(
            @PathVariable Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        int likeCount = articleLikeService.addArticleLike(articleId, boardPrincipal.email());

        return ApiResponse.success(likeCount);
    }

    /**
     * 특정 게시글의 좋아요 해제
     */
    @DeleteMapping("/{articleId}")
    public ApiResponse<Integer> deleteArticleLike(
            @PathVariable Long articleId
    ) {
        int likeCount = articleLikeService.deleteArticleLike(articleId);

        return ApiResponse.success(likeCount);
    }
}
