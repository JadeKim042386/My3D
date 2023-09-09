package joo.project.my3d.repository;

import joo.project.my3d.domain.ArticleFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArticleFileRepository extends JpaRepository<ArticleFile, Long> {

    Optional<ArticleFile> findByFileName(String fileName);

    void deleteByArticleId(Long articleId);

    List<ArticleFile> findByArticleId(Long articleId);
}
