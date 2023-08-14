package joo.project.my3d.service;

import com.querydsl.core.types.Predicate;
import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.ArticleFile;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.dto.ArticleDto;
import joo.project.my3d.dto.ArticleFileDto;
import joo.project.my3d.dto.ArticleWithCommentsAndLikeCountDto;
import joo.project.my3d.exception.ArticleException;
import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.exception.FileException;
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

    public Page<ArticleDto> getArticles(Predicate predicate, Pageable pageable) {

        return articleRepository.findAll(predicate, pageable).map(ArticleDto::from);
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

    @Transactional
    public void saveArticle(ArticleDto articleDto) {
        //모델 게시글일 경우 Category가 있어야함
        Article article = articleDto.toEntity();
        if (article.getArticleType() == ArticleType.MODEL && article.getArticleCategory() == null) {
            throw new ArticleException(ErrorCode.ARTICLE_CATEGORY_NOT_FOUND);
        }
        articleRepository.save(article);
    }

    @Transactional
    public void updateArticle(Long articleId, ArticleDto articleDto) {
        try {
            Article article = articleRepository.getReferenceById(articleId);
            UserAccount userAccount = userAccountRepository.getReferenceById(articleDto.userAccountDto().userId());
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
                if (articleDto.articleFileDto() != null) {
                    article.setArticleFile(articleDto.articleFileDto().toEntity());
                }
            } else {
                log.error("작성자와 수정자가 다릅니다. 작성자: {}, 수정자: {}",
                        article.getUserAccount().getUserId(),
                        userAccount.getUserId());
                throw new ArticleException(ErrorCode.ARTICLE_NOT_WRITER);
            }
        } catch (EntityNotFoundException e) {
            throw new ArticleException(ErrorCode.ARTICLE_NOT_FOUND);
        }
    }

    @Transactional
    public void deleteArticle(Long articleId) {
        //TODO: 작성자 또는 Admin이 게시글을 삭제 할 수 있음

        //게시글에 속한 댓글, 좋아요도 같이 삭제
        articleCommentRepository.deleteByArticleId(articleId);
        articleLikeRepository.deleteByArticleId(articleId);
        articleRepository.deleteById(articleId);
    }

    public ArticleFileDto getArticleFile(Long articleId) {
        return articleRepository.findById(articleId)
                .map(article -> ArticleFileDto.from(article.getArticleFile()))
                .orElseThrow(() -> new FileException(ErrorCode.FILE_NOT_FOUND));
    }
}
