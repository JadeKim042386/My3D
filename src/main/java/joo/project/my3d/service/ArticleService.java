package joo.project.my3d.service;

import joo.project.my3d.domain.Article;
import joo.project.my3d.dto.ArticleDto;
import joo.project.my3d.dto.ArticleWithCommentsAndLikeCountDto;
import joo.project.my3d.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;

    public List<Article> getArticles(Pageable pageable) {
        return List.of();
    }

    public ArticleWithCommentsAndLikeCountDto getArticle(Long articleId) {
        return null;
    }

    @Transactional
    public void saveArticle(ArticleDto articleDto) {
        //TODO: ArticleType=MODEL일 경우 Category가 있어야함

    }

    @Transactional
    public void updateArticle(ArticleDto articleDto) {
        //TODO: 작성자가 게시글을 수정할 수 있음
    }

    @Transactional
    public void deleteArticle(Long articleId) {
        //TODO: 작성자 또는 Admin이 게시글을 삭제 할 수 있음
    }
}
