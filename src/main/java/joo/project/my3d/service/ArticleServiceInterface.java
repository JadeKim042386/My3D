package joo.project.my3d.service;

import com.querydsl.core.types.Predicate;
import joo.project.my3d.domain.Article;
import joo.project.my3d.dto.ArticleFormDto;
import joo.project.my3d.dto.ArticlePreviewDto;
import joo.project.my3d.dto.request.ArticleFormRequest;
import joo.project.my3d.dto.response.ArticleDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleServiceInterface {
    /**
     * 게시판에 표시할 전체 게시글 조회
     */
    Page<ArticlePreviewDto> getArticlesForPreview(Predicate predicate, Pageable pageable);

    /**
     * 게시판 추가/수정을 위한 조회
     */
    ArticleFormDto getArticleForm(Long articleId);
    /**
     * 게시글 상세 정보 조회
     */
    ArticleDetailResponse getArticleWithComments(Long articleId, Long userAccountId);

    /**
     * 게시글 저장
     */
    Article saveArticle(Long userAccountId, ArticleFormRequest articleFormRequest);
    /**
     * 게시글 수정
     * @param articleFormRequest 게시글 수정 입력 정보
     * @param articleId 수정하려는 게시글 ID
     * @param requestUserAccountId 수정하려는 사용자의 id
     */
    void updateArticle(ArticleFormRequest articleFormRequest, Long articleId, Long requestUserAccountId);

    /**
     * 게시글 삭제
     */
    void deleteArticle(Long articleId, Long userAccountId);

    /**
     * articleId에 해당하는 article의 작성자가 userAccountId인지 확인
     */
    boolean isExistsByArticleIdAndUserAccountId(Long articleId, Long userAccountId);
}
