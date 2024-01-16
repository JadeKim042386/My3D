package joo.project.my3d.repository;

import joo.project.my3d.domain.ArticleFile;
import joo.project.my3d.repository.mapping.FileNameInterface;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleFileRepository extends JpaRepository<ArticleFile, Long> {
    Optional<ArticleFile> findByArticleId(Long articleId);

    Optional<FileNameInterface> findFileNameByArticleId(Long articleId);
}
