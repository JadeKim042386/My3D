package joo.project.my3d.controller;

import com.querydsl.core.types.Predicate;
import joo.project.my3d.aop.TimeTrace;
import joo.project.my3d.config.AppConfig;
import joo.project.my3d.domain.Article;
import joo.project.my3d.dto.response.ArticleFormResponse;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.service.AlarmServiceInterface;
import joo.project.my3d.service.ArticleServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Objects;

import static joo.project.my3d.domain.constant.FormStatus.CREATE;
import static joo.project.my3d.domain.constant.FormStatus.UPDATE;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class ArticlesController {
    private final ArticleServiceInterface articleService;
    private final AlarmServiceInterface<SseEmitter> alarmService;

    /**
     * 게시판 페이지
     * 파일 경로, 카테고리 리스트, 페이지네이션 바 숫자 리스트는 프론트엔드에서 처리
     *
     * {
     *     content: ArticlePreviewDto,
     *     page: int,
     *     size: int,
     *     totalElements: long,
     *     totalPages: int,
     *     first: boolean,
     *     last: boolean
     * }
     */
    @TimeTrace
    @GetMapping
    public String articleBoard(
            @PageableDefault(size = 9, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @QuerydslPredicate(root = Article.class) Predicate predicate,
            Model model) {
        model.addAttribute("articles", articleService.getArticlesForPreview(predicate, pageable));
        model.addAttribute("filePath", AppConfig.filePath);
        // 파일 경로, 카테고리 리스트, 페이지네이션 바 숫자 리스트는 프론트엔드에서 처리
        return "index";
    }

    /**
     * 게시글 작성 페이지
     *
     * {
     *     id: null,
     *     formStatus: CREATE,
     *     modelFile: null,
     *     title: null,
     *     content: null,
     *     articleCategory: null
     * }
     */
    @GetMapping("/form")
    public String articleAddForm(Model model) {
        model.addAttribute("article", ArticleFormResponse.of(CREATE));
        return "articles/form";
    }

    /**
     * 게시글 수정 페이지
     *
     * {
     *     id: long,
     *     formStatus: UPDATE,
     *     modelFile: ArticleFileWithDimensionDto,
     *     title: String,
     *     content: String,
     *     articleCategory: String
     * }
     */
    @TimeTrace
    @GetMapping("/{articleId}/form")
    public String articleUpdateForm(@PathVariable Long articleId, Model model) {
        model.addAttribute("article", ArticleFormResponse.from(articleService.getArticleForm(articleId), UPDATE));
        return "articles/form";
    }

    /**
     * 게시글 페이지
     *
     * {
     *     article: ArticleWithCommentsDto,
     *     likeCount: int,
     *     addedLike: boolean
     * }
     */
    @TimeTrace
    @GetMapping("/{articleId}")
    public String articleDetail(
            @PathVariable Long articleId,
            @RequestParam(required = false) Long alarmId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            Model model) {
        // 알람을 통해 이동될 경우 읽음 처리
        if (!Objects.isNull(alarmId)) {
            alarmService.checkAlarm(alarmId);
        }
        model.addAttribute("article", articleService.getArticleWithComments(articleId, boardPrincipal.id()));
        model.addAttribute("filePath", AppConfig.filePath);
        return "articles/detail";
    }
}
