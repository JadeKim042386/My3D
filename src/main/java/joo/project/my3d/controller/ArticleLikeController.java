package joo.project.my3d.controller;

import joo.project.my3d.dto.response.ArticleLikeResponse;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.service.ArticleLikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<ArticleLikeResponse> addArticleLike(
            @PathVariable Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        return ResponseEntity.ok(
                ArticleLikeResponse.of(
                        articleLikeService.addArticleLike(articleId, boardPrincipal.email())
                )
        );
    }

    /**
     * 특정 게시글의 좋아요 해제
     */
    @DeleteMapping("/{articleId}")
    public ResponseEntity<ArticleLikeResponse> deleteArticleLike(
            @PathVariable Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(
                        ArticleLikeResponse.of(
                                articleLikeService.deleteArticleLike(articleId, boardPrincipal.email())
                        )
                );
    }
}
