package joo.project.my3d.service;

import com.querydsl.core.types.Predicate;
import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.dto.ArticleDto;
import joo.project.my3d.dto.ArticleFormDto;
import joo.project.my3d.dto.ArticlePreviewDto;
import joo.project.my3d.dto.ArticleWithCommentsDto;
import joo.project.my3d.dto.request.ArticleFormRequest;
import joo.project.my3d.exception.ArticleException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.repository.ArticleCommentRepository;
import joo.project.my3d.repository.ArticleLikeRepository;
import joo.project.my3d.repository.ArticleRepository;
import joo.project.my3d.repository.UserAccountRepository;
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
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final UserAccountRepository userAccountRepository;
    private final ArticleFileService articleFileService;

    /**
     * 게시판에 표시할 전체 게시글 조회
     */
    public Page<ArticlePreviewDto> getArticlesForPreview(Predicate predicate, Pageable pageable) {

        return articleRepository.findAll(predicate, pageable).map(ArticlePreviewDto::from);
    }

    /**
     * 게시글 추가/수정을 위한 조회 (댓글과 좋아요 개수를 제외)
     * @throws ArticleException 게시글을 찾을 수 없을 경우 발생하는 예외
     */
    public ArticleFormDto getArticleForm(Long articleId) {
        return articleRepository.findByIdFetchForm(articleId)
                .map(ArticleFormDto::from)
                .orElseThrow(() -> new ArticleException(ErrorCode.ARTICLE_NOT_FOUND));
    }

    /**
     * 상세 정보를 포함한 게시글 조회 (댓글과 좋아요 개수를 포함)
     * @throws ArticleException 게시글을 찾을 수 없을 경우 발생하는 예외
     */
    public ArticleWithCommentsDto getArticleWithComments(Long articleId) {
        return articleRepository.findByIdFetchDetail(articleId)
                .map(ArticleWithCommentsDto::from)
                .orElseThrow(() -> new ArticleException(ErrorCode.ARTICLE_NOT_FOUND));
    }

    /**
     * @param email 작성자 이메일
     */
    @Transactional
    public Article saveArticle(String email, ArticleDto articleDto) {
        //모델 게시글일 경우 Category가 있어야함
        UserAccount userAccount = userAccountRepository.getReferenceByEmail(email);
        Article article = articleDto.toEntity(userAccount);

        return articleRepository.save(article);
    }

    /**
     * @param articleFormRequest 게시글 수정 입력 정보
     * @param articleId 수정하려는 게시글 ID
     * @param requestEmail 수정하려는 사용자의 이메일
     * @throws ArticleException 게시글 작성자와 수정자가 다를 경우 또는 게시글이 DB에 존재하지 않는 경우 발생하는 예외
     */
    @Transactional
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
    public void deleteArticle(Long articleId, String email) {
        try {
            Article article = articleRepository.getReferenceById(articleId); //작성자
            //작성자와 삭제를 요청한 유저가 같은지 확인
            if (article.getUserAccount().getEmail().equals(email)) {
                //게시글에 속한 댓글, 좋아요도 같이 삭제
                //TODO: comment, like를 먼저 삭제해야되는 것인지 다시 확인 필요
                articleCommentRepository.deleteByArticleId(articleId);
                articleLikeRepository.deleteByArticleId(articleId);
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
