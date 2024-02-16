package joo.project.my3d.repository;

import joo.project.my3d.domain.ArticleComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
    boolean existsByIdAndUserAccountId(Long articleCommentId, Long userAccountId);

    @Query("select ac.article.id from ArticleComment ac where ac.id = ?1")
    Optional<Long> findArticleIdById(Long articleCommentId);
}
