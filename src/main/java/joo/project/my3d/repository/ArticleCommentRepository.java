package joo.project.my3d.repository;

import joo.project.my3d.domain.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
    List<ArticleComment> findByArticleId(Long articleId);

    void deleteByArticleId(Long articleId);

    void deleteByUserAccount_Email(String email);
}
