package joo.project.my3d.repository;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.constant.ArticleCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findByArticleCategory(ArticleCategory articleCategory, Pageable pageable);

    List<Article> findAllByUserAccount_UserId(String userId);
}
