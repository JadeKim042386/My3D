package joo.project.my3d.api;

import joo.project.my3d.dto.response.ArticleLikeResponse;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.exception.ArticleLikeException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.service.ArticleLikeServiceInterface;
import joo.project.my3d.service.ArticleServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/articles/{articleId}/like")
@RequiredArgsConstructor
public class ArticleLikeApi {

    private final ArticleServiceInterface articleService;
    private final ArticleLikeServiceInterface articleLikeService;

    /**
     * 특정 게시글에 좋아요 추가
     */
    @PostMapping
    public ResponseEntity<ArticleLikeResponse> addArticleLike(
            @PathVariable Long articleId, @AuthenticationPrincipal BoardPrincipal boardPrincipal) {
        // 게시글 작성자는 좋아요를 요청할 수 없음
        isWriter(articleId, boardPrincipal.id());
        isPossible(articleId, boardPrincipal.id(), true);
        return ResponseEntity.ok(
                ArticleLikeResponse.of(articleLikeService.addArticleLike(articleId, boardPrincipal.id())));
    }

    /**
     * 특정 게시글의 좋아요 해제
     */
    @DeleteMapping
    public ResponseEntity<ArticleLikeResponse> deleteArticleLike(
            @PathVariable Long articleId, @AuthenticationPrincipal BoardPrincipal boardPrincipal) {
        // 게시글 작성자는 좋아요를 취소할 수 없음
        isWriter(articleId, boardPrincipal.id());
        isPossible(articleId, boardPrincipal.id(), false);
        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(ArticleLikeResponse.of(articleLikeService.deleteArticleLike(articleId, boardPrincipal.id())));
    }

    /**
     * 작성자는 좋아요를 추가하거나 취소할 수 없음
     */
    private void isWriter(Long articleId, Long userAccountId) {
        if (articleService.isExistsByArticleIdAndUserAccountId(articleId, userAccountId)) {
            throw new ArticleLikeException(ErrorCode.INVALID_REQUEST);
        }
    }

    /**
     * 좋아요를 추가했을 경우만 삭제할 수 있고, 좋아요를 추가하지 않았을 경우에만 추가할 수 있음
     */
    private void isPossible(Long articleId, Long userAccountId, boolean requestAdd) {
        boolean addedLike = articleLikeService.addedLike(articleId, userAccountId);
        if ((requestAdd && addedLike) || (!requestAdd && !addedLike)) {
            throw new ArticleLikeException(ErrorCode.ALREADY_LIKE_ADD_OR_DELETE);
        }
    }
}
