package joo.project.my3d.repository;

import joo.project.my3d.domain.ArticleFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ArticleFileRepository extends JpaRepository<ArticleFile, Long> {
    Optional<ArticleFile> findByArticleId(Long articleId);

    @Query("select af.fileName from ArticleFile af where af.article.id = ?1")
    Optional<String> findFileNameByArticleId(Long articleId);
}
