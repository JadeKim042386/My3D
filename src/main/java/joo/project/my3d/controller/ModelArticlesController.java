package joo.project.my3d.controller;

import com.querydsl.core.types.Predicate;
import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.ArticleLike;
import joo.project.my3d.domain.GoodOption;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.domain.constant.FormStatus;
import joo.project.my3d.dto.ArticleDto;
import joo.project.my3d.dto.ArticleFileDto;
import joo.project.my3d.dto.DimensionDto;
import joo.project.my3d.dto.GoodOptionDto;
import joo.project.my3d.dto.request.ArticleFormRequest;
import joo.project.my3d.dto.request.DimensionRequest;
import joo.project.my3d.dto.request.GoodOptionRequest;
import joo.project.my3d.dto.response.ArticleFormResponse;
import joo.project.my3d.dto.response.ArticleResponse;
import joo.project.my3d.dto.response.ArticleWithCommentsAndLikeCountResponse;
import joo.project.my3d.dto.security.BoardPrincipal;
import joo.project.my3d.repository.ArticleLikeRepository;
import joo.project.my3d.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/model_articles")
@RequiredArgsConstructor
public class ModelArticlesController {

    private final ArticleService articleService;
    private final PaginationService paginationService;
    private final ArticleFileService articleFileService;
    private final ArticleLikeRepository articleLikeRepository;
    private final GoodOptionService goodOptionService;
    private final DimensionService dimensionService;

    @Value("${model.rel-path}")
    private String relModelPath;

    @GetMapping
    public String articles(
            @PageableDefault(size=9, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @QuerydslPredicate(root = Article.class) Predicate predicate,
            Model model
    ) {
        Page<ArticleDto> articles = articleService.getArticles(predicate, pageable);
        List<Integer> barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());
        if (articles.isEmpty()) {
            model.addAttribute(
                "articles",
                Page.empty()
            );
        } else {
            model.addAttribute(
                "articles",
                new PageImpl<>(
                    articles
                    .filter(articleDto -> articleDto.articleType() == ArticleType.MODEL)
                    .map(articleDto -> {
                        ArticleFileDto articleFileDto = articleFileService.getArticleFiles(articleDto.id())
                                .stream().filter(ArticleFileDto::isModelFile)
                                .toList().get(0);
                        return ArticleResponse.from(articleDto, articleFileDto);
                    })
                    .toList(),
                    pageable,
                    articles.getTotalElements()
                )
            );
        }

        model.addAttribute("modelPath", relModelPath);
        model.addAttribute("categories", ArticleCategory.values());
        model.addAttribute("paginationBarNumbers", barNumbers);

        return "model_articles/index";
    }

    @GetMapping("/{articleId}")
    public String article(
            @PathVariable Long articleId,
            Model model,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        Optional<ArticleLike> articleLike = articleLikeRepository.findByUserAccount_EmailAndArticle_Id(boardPrincipal.email(), articleId);
        ArticleWithCommentsAndLikeCountResponse article = ArticleWithCommentsAndLikeCountResponse.from(articleService.getArticleWithComments(articleId));

        model.addAttribute("article", article);
        model.addAttribute("articleComments", article.articleCommentResponses());
        model.addAttribute("modelFile", article.modelFile());
        model.addAttribute("imgFiles", article.imgFiles());
        model.addAttribute("addedLike", articleLike.isPresent());
        model.addAttribute("modelPath", relModelPath);

        return "model_articles/detail";
    }

    @GetMapping("/form")
    public String articleForm(Model model) {
        model.addAttribute("article", ArticleFormResponse.of(null, null, List.of(), null, null, null, null, null, null));
        model.addAttribute("formStatus", FormStatus.CREATE);
        model.addAttribute("categories", ArticleCategory.values());
        return "model_articles/form";
    }

    @PostMapping("/form")
    public String postNewArticle(
            @ModelAttribute("article") @Validated ArticleFormRequest articleFormRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            log.warn("bindingResult={}", bindingResult);
            model.addAttribute("formStatus", FormStatus.CREATE);
            model.addAttribute("categories", ArticleCategory.values());
            return "/model_articles/form";
        }

        //게시글 저장
        Article article = articleService.saveArticle(
                articleFormRequest.toArticleDto(
                        boardPrincipal.toDto(),
                        ArticleType.MODEL
                )
        );
        //파일 저장
        List<MultipartFile> files = articleFormRequest.getFiles();
        for (MultipartFile file : files) {
            if (file.getSize() > 0) {
                articleFileService.saveArticleFile(article, file);
            }
        }
        //상품 옵션 저장
        List<GoodOptionRequest> goodOptionRequests = articleFormRequest.getGoodOptionRequests();
        for (GoodOptionRequest goodOptionRequest : goodOptionRequests){
            GoodOptionDto goodOptionDto = goodOptionRequest.toDto(article.getId());
            GoodOption goodOption = goodOptionService.saveGoodOption(goodOptionDto);
            //치수 저장
            List<DimensionDto> dimensionDtos = goodOptionRequest.toDimensionDtos(goodOption.getId());
            for (DimensionDto dimensionDto : dimensionDtos) {
                dimensionService.saveDimension(dimensionDto);
            }
        }

        return "redirect:/model_articles";
    }

    @GetMapping("/form/{articleId}")
    public String updateArticle(
            @PathVariable Long articleId,
            Model model
    ) {
        List<GoodOptionRequest> goodOptionRequests = goodOptionService.getGoodOptions(articleId).stream()
                .map(GoodOptionRequest::from)
                .toList();
        List<DimensionRequest> dimensionRequests = dimensionService.getDimensions(articleId).stream()
                .map(DimensionRequest::from)
                .toList();

        model.addAttribute(
            "article",
            ArticleFormResponse.from(
                articleService.getArticle(articleId),
                articleFileService.getArticleFiles(articleId),
                goodOptionRequests,
                dimensionRequests
            )
        );
        model.addAttribute("formStatus", FormStatus.UPDATE);
        model.addAttribute("categories", ArticleCategory.values());
        return "model_articles/form";
    }

    @PostMapping("/form/{articleId}")
    public String postUpdateArticle(
            @PathVariable Long articleId,
            @ModelAttribute("article") @Validated ArticleFormRequest articleFormRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            log.warn("formBindingResult={}", bindingResult);
            model.addAttribute("formStatus", FormStatus.UPDATE);
            model.addAttribute("categories", ArticleCategory.values());
            return "model_articles/form";
        }

        //파일 업데이트
        List<ArticleFileDto> articleFileDtos = articleService.getArticleFiles(articleId); //저장되어있는 파일들
        Article article = articleService.getArticle(articleId).toEntity();
        List<MultipartFile> files = articleFormRequest.getFiles();
        boolean isUpdated = articleFileService.updateArticleFile(article, files, articleFileDtos);
        //업데이트되었다면 이전에 저장한 파일 모두 삭제하고 업데이트된 파일들을 저장
        if (isUpdated) {
            for (ArticleFileDto articleFile : articleFileDtos) {
                articleFileService.deleteArticleFile(articleFile.id());
            }
            for (MultipartFile file : files) {
                articleFileService.saveArticleFile(article, file);
            }
        }
        //상품옵션 업데이트
        goodOptionService.deleteGoodOptions(articleId);
        List<GoodOptionRequest> goodOptionRequests = articleFormRequest.getGoodOptionRequests();
        for (GoodOptionRequest goodOptionRequest : goodOptionRequests) {
            GoodOption goodOption = goodOptionService.saveGoodOption(goodOptionRequest.toDto(articleId));
            //치수 업데이트
            dimensionService.deleteDimensions(goodOption.getId());
            List<DimensionDto> dimensionDtos = goodOptionRequest.toDimensionDtos(goodOption.getId());
            for (DimensionDto dimensionDto : dimensionDtos) {
                dimensionService.saveDimension(dimensionDto);
            }
        }

        articleService.updateArticle(
            articleId,
            articleFormRequest.toArticleDto(
                boardPrincipal.toDto()
            )
        );
        return "redirect:/model_articles";
    }

    @PostMapping("{articleId}/delete")
    public String deleteArticle(
            @PathVariable Long articleId,
            @AuthenticationPrincipal BoardPrincipal boardPrincipal
    ) {
        articleService.deleteArticle(articleId, boardPrincipal.email());

        return "redirect:/model_articles";
    }
}
