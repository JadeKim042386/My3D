package joo.project.my3d.repository;

import joo.project.my3d.domain.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    void deleteByArticleId(Long articleId);

    void deleteByUserAccount_UserId(String userId);
}
