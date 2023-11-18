package joo.project.my3d.service;

import com.querydsl.core.types.Predicate;
import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.dto.ArticleDto;
import joo.project.my3d.dto.ArticleWithCommentsAndLikeCountDto;
import joo.project.my3d.dto.ArticlesDto;
import joo.project.my3d.exception.ArticleException;
import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.repository.ArticleCommentRepository;
import joo.project.my3d.repository.ArticleLikeRepository;
import joo.project.my3d.repository.ArticleRepository;
import joo.project.my3d.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final UserAccountRepository userAccountRepository;

    public Page<ArticlesDto> getArticles(Predicate predicate, Pageable pageable) {

        return articleRepository.findAll(predicate, pageable).map(ArticlesDto::from);
    }

    public ArticleDto getArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleDto::from)
                .orElseThrow(() -> new ArticleException(ErrorCode.ARTICLE_NOT_FOUND));
    }

    public ArticleWithCommentsAndLikeCountDto getArticleWithComments(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleWithCommentsAndLikeCountDto::from)
                .orElseThrow(() -> new ArticleException(ErrorCode.ARTICLE_NOT_FOUND));
    }

    /**
     * @throws ArticleException 게시글에 카테고리를 지정하지 않았을 경우 발생하는 예외
     */
    @Transactional
    public Article saveArticle(ArticleDto articleDto) {
        //모델 게시글일 경우 Category가 있어야함
        Article article = articleDto.toEntity();
        if (article.getArticleType() == ArticleType.MODEL && article.getArticleCategory() == null) {
            throw new ArticleException(ErrorCode.ARTICLE_CATEGORY_NOT_FOUND);
        }
        return articleRepository.save(article);
    }

    /**
     * @throws ArticleException 게시글 작성자와 수정자가 다를 경우 또는 게시글이 DB에 존재하지 않는 경우 발생하는 예외
     */
    @Transactional
    public void updateArticle(Long articleId, ArticleDto articleDto) {
        try {
            Article article = articleRepository.getReferenceById(articleId); //작성자
            UserAccount userAccount = userAccountRepository.getReferenceByEmail(articleDto.userAccountDto().email()); //수정자
            //작성자와 수정자가 같은지 확인
            if (article.getUserAccount().equals(userAccount)) {
                if (articleDto.title() != null) {
                    article.setTitle(articleDto.title());
                }
                if (articleDto.content() != null) {
                    article.setContent(articleDto.content());
                }
                if (articleDto.articleType() != null) {
                    article.setArticleType(articleDto.articleType());
                }
                if (articleDto.articleType() == ArticleType.MODEL && articleDto.articleCategory() != null) {
                    article.setArticleCategory(articleDto.articleCategory());
                }
            } else {
                log.error("작성자와 수정자가 다릅니다. 작성자: {}, 수정자: {}",
                        article.getUserAccount().getEmail(),
                        userAccount.getEmail());
                throw new ArticleException(ErrorCode.NOT_WRITER);
            }
        } catch (EntityNotFoundException e) {
            throw new ArticleException(ErrorCode.ARTICLE_NOT_FOUND);
        }
    }

    @Transactional
    public void deleteArticle(Long articleId, String email) {
        Article article = articleRepository.getReferenceById(articleId); //작성자
        //작성자와 삭제를 요청한 유저가 같은지 확인
        if (article.getUserAccount().getEmail().equals(email)) {
            //게시글에 속한 댓글, 좋아요도 같이 삭제
            articleCommentRepository.deleteByArticleId(articleId);
            articleLikeRepository.deleteByArticleId(articleId);
            articleRepository.delete(article);
        }
    }
}
