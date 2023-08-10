package joo.project.my3d.service;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import joo.project.my3d.dto.ArticleDto;
import joo.project.my3d.dto.ArticleWithCommentsAndLikeCountDto;
import joo.project.my3d.exception.ArticleException;
import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.repository.ArticleCommentRepository;
import joo.project.my3d.repository.ArticleLikeRepository;
import joo.project.my3d.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleCommentRepository articleCommentRepository;

    public Page<ArticleDto> getArticles(Pageable pageable) {
        return articleRepository.findAll(pageable).map(ArticleDto::from);
    }

    public Page<ArticleDto> getArticlesByArticleCategory(ArticleCategory articleCategory, Pageable pageable) {
        return articleRepository.findByArticleCategory(articleCategory, pageable).map(ArticleDto::from);
    }

    public ArticleWithCommentsAndLikeCountDto getArticle(Long articleId) {
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
    public void updateArticle(ArticleDto articleDto) {
        //TODO: 작성자가 게시글을 수정할 수 있음
        try {
            Article article = articleRepository.getReferenceById(articleDto.id());
            // title, content, articleType, articleCategory, articleFile
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
}
