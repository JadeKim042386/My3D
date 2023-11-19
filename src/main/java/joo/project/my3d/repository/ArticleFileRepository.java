package joo.project.my3d.repository;

import joo.project.my3d.domain.ArticleFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleFileRepository extends JpaRepository<ArticleFile, Long> {
    void deleteByArticleId(Long articleId);

    ArticleFile findByArticleId(Long articleId);

    ArticleFile getReferenceByArticle_Id(Long articleId);
}
