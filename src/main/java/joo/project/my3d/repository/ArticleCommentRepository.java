package joo.project.my3d.repository;

import joo.project.my3d.domain.ArticleComment;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.LOAD;

public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {
    boolean existsByIdAndUserAccountId(Long articleCommentId, Long userAccountId);

    @EntityGraph(attributePaths = "childComments", type = LOAD)
    @Query("select ac from ArticleComment ac where ac.id = ?1")
    Optional<ArticleComment> findByFetchChildComments(Long commentId);
}
