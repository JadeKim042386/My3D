package joo.project.my3d.repository;

import joo.project.my3d.domain.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    void deleteByArticleId(Long articleId);
    void deleteByUserAccount_UserId(String userId);

    Optional<ArticleLike> findByUserAccount_UserIdAndArticle_Id(String username, Long articleId);
}
