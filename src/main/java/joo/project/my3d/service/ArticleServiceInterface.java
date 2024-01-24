package joo.project.my3d.service;

import com.querydsl.core.types.Predicate;
import joo.project.my3d.domain.Article;
import joo.project.my3d.dto.ArticleDto;
import joo.project.my3d.dto.ArticleFormDto;
import joo.project.my3d.dto.ArticlePreviewDto;
import joo.project.my3d.dto.request.ArticleFormRequest;
import joo.project.my3d.dto.response.ArticleDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

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
    ArticleDetailResponse getArticleWithComments(Long articleId, String email);

    /**
     * 게시글 저장
     */
    Article saveArticle(String email, ArticleDto articleDto, MultipartFile file);
    /**
     * 게시글 수정
     * @param articleFormRequest 게시글 수정 입력 정보
     * @param articleId 수정하려는 게시글 ID
     * @param requestEmail 수정하려는 사용자의 이메일
     */
    void updateArticle(ArticleFormRequest articleFormRequest, Long articleId, String requestEmail);

    /**
     * 게시글 삭제
     */
    void deleteArticle(Long articleId, String email);
}
