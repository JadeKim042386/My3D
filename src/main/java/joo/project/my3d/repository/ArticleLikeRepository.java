package joo.project.my3d.repository;

import joo.project.my3d.domain.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    void deleteByArticleIdAndUserAccountId(Long articleId, Long userAccountId);

    int countByArticleId(Long articleId);

    boolean existsByArticleIdAndUserAccountId(Long articleId, Long userAccountId);
}
