package joo.project.my3d.api;

import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.dto.ArticleCommentDto;
import joo.project.my3d.dto.request.ArticleCommentRequest;
import joo.project.my3d.dto.response.ApiResponse;
import joo.project.my3d.dto.response.ArticleCommentResponse;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.service.ArticleCommentServiceInterface;
import joo.project.my3d.service.UserAccountServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/articles/{articleId}/comments")
@RequiredArgsConstructor
public class ArticleCommentsApi {

    private final ArticleCommentServiceInterface articleCommentService;
    private final UserAccountServiceInterface userAccountService;

    /**
     * 댓글 추가
     */
    @PostMapping
    public ResponseEntity<ArticleCommentResponse> postNewComment(
            @PathVariable Long articleId,
            ArticleCommentRequest articleCommentRequest,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal) {
        UserAccount commentWriter = userAccountService.searchUserEntity(boardPrincipal.email());
        UserAccount articleWriter = userAccountService.searchUserEntityByArticleId(articleId);
        ArticleCommentDto commentDto = articleCommentService.saveComment(
                articleCommentRequest.toDto(articleId, boardPrincipal.nickname(), boardPrincipal.email()),
                commentWriter,
                articleWriter);

        return ResponseEntity.status(HttpStatus.CREATED).body(ArticleCommentResponse.from(commentDto));
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse> deleteComment(
            @PathVariable Long commentId, @AuthenticationPrincipal BoardPrincipal boardPrincipal) {
        articleCommentService.deleteComment(commentId, boardPrincipal.id());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.of("You successfully deleted comment"));
    }
}
