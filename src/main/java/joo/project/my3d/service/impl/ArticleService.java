package joo.project.my3d.service.impl;

import com.querydsl.core.types.Predicate;
import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.DimensionOption;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.dto.ArticleFormDto;
import joo.project.my3d.dto.ArticlePreviewDto;
import joo.project.my3d.dto.ArticleWithCommentsDto;
import joo.project.my3d.dto.request.ArticleFormRequest;
import joo.project.my3d.dto.response.ArticleDetailResponse;
import joo.project.my3d.exception.ArticleException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.repository.ArticleRepository;
import joo.project.my3d.repository.UserAccountRepository;
import joo.project.my3d.service.ArticleFileServiceInterface;
import joo.project.my3d.service.ArticleLikeServiceInterface;
import joo.project.my3d.service.ArticleServiceInterface;
import joo.project.my3d.service.FileServiceInterface;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleService implements ArticleServiceInterface {

    private final ArticleRepository articleRepository;
    private final UserAccountRepository userAccountRepository;
    private final ArticleFileServiceInterface articleFileService;
    private final ArticleLikeServiceInterface articleLikeService;
    private final FileServiceInterface fileService;

    @Override
    public Page<ArticlePreviewDto> getArticlesForPreview(Predicate predicate, Pageable pageable) {

        return articleRepository.findAll(predicate, pageable).map(ArticlePreviewDto::from);
    }

    /**
     * @throws ArticleException 게시글을 찾을 수 없을 경우 발생하는 예외
     */
    @Override
    public ArticleFormDto getArticleForm(Long articleId) {
        return articleRepository.findByIdFetchForm(articleId)
                .map(ArticleFormDto::from)
                .orElseThrow(() -> new ArticleException(ErrorCode.ARTICLE_NOT_FOUND));
    }

    /**
     * @throws ArticleException 게시글을 찾을 수 없을 경우 발생하는 예외
     */
    @Override
    public ArticleDetailResponse getArticleWithComments(Long articleId, String email) {
        return articleRepository.findByIdFetchDetail(articleId)
                .map(article -> ArticleDetailResponse.of(
                        ArticleWithCommentsDto.from(article),
                        articleLikeService.getLikeCountByArticleId(articleId),
                        articleLikeService.addedLike(articleId, email)
                        )
                )
                .orElseThrow(() -> new ArticleException(ErrorCode.ARTICLE_NOT_FOUND));
    }

    @Transactional
    @Override
    public Article saveArticle(String email, ArticleFormRequest articleFormRequest) {
        UserAccount userAccount = userAccountRepository.getReferenceByEmail(email);
        Article savedArticle = articleRepository.save(
                articleFormRequest.toArticleEntity(userAccount, ArticleType.MODEL)
        );
        DimensionOption dimensionOption = savedArticle.getArticleFile().getDimensionOption();
        dimensionOption.getDimensions().addAll(
                articleFormRequest.getDimensionOptions().get(0).toDimensionEntities(dimensionOption)
        );
        fileService.uploadFile(
                articleFormRequest.getModelFile(),
                savedArticle.getArticleFile().getFileName()
        );
        log.debug("{}에 의해 게시글이 저장되었습니다.", email);
        return savedArticle;
    }

    /**
     * @throws ArticleException 게시글 작성자와 수정자가 다를 경우 또는 게시글이 DB에 존재하지 않는 경우 발생하는 예외
     */
    @Transactional
    @Override
    public void updateArticle(ArticleFormRequest articleFormRequest, Long articleId, String requestEmail) {
        Article article = articleRepository.findByIdAndUserAccount_Email(articleId, requestEmail)
                .orElseThrow(() -> new ArticleException(ErrorCode.ARTICLE_NOT_FOUND)); //작성자

        //작성자와 수정자가 같은지 확인
        if (Objects.equals(article.getUserAccount().getEmail(), requestEmail)) {
            //파일 수정
            articleFileService.updateArticleFile(articleFormRequest, articleId);
            //게시글 수정
            Optional.ofNullable(articleFormRequest.getTitle())
                    .ifPresent(article::setTitle);
            Optional.ofNullable(articleFormRequest.getContent())
                    .ifPresent(article::setContent);
            Optional.ofNullable(articleFormRequest.getArticleCategory())
                    .ifPresent(category -> article.setArticleCategory(ArticleCategory.valueOf(category)));
        } else {
            log.error("작성자와 수정자가 다릅니다. 작성자: {}, 수정자: {}",
                    article.getUserAccount().getEmail(), requestEmail);
            throw new ArticleException(ErrorCode.NOT_WRITER);
        }
    }

    /**
     * @throws ArticleException 게시글을 찾을 수 없거나 삭제에 실패했을 경우 발생하는 예외
     */
    @Transactional
    @Override
    public void deleteArticle(Long articleId, String email) {
        try {
            Article article = articleRepository.getReferenceById(articleId); //작성자
            //작성자와 삭제를 요청한 유저가 같은지 확인
            if (article.getUserAccount().getEmail().equals(email)) {
                //S3 파일 삭제
                articleFileService.deleteFile(articleId);
                //게시글에 속한 댓글, 좋아요도 같이 삭제
                article.deleteAll();
                articleRepository.delete(article);
            }else {
                log.error("작성자와 수정자가 다릅니다. 작성자: {}, 삭제 요청자: {}",
                        article.getUserAccount().getEmail(), email);
                throw new ArticleException(ErrorCode.NOT_WRITER);
            }
        } catch (EntityNotFoundException e) {
            throw new ArticleException(ErrorCode.ARTICLE_NOT_FOUND, e);
        } catch (IllegalArgumentException e) {
            throw new ArticleException(ErrorCode.FAILED_DELETE, e);
        } catch (OptimisticLockingFailureException e) {
            throw new ArticleException(ErrorCode.CONFLICT_DELETE, e);
        }
    }
}
