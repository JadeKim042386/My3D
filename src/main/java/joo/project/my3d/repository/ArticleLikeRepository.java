package joo.project.my3d.repository;

import joo.project.my3d.domain.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    void deleteByArticleId(Long articleId);
    void deleteByArticleIdAndUserAccount_Email(Long articleId, String email);
    //TODO: 테스트에서만 사용되고 있음
    void deleteByUserAccount_Email(String email);
    int countByArticleId(Long articleId);
    boolean existsByArticleIdAndUserAccount_Email(Long articleId, String email);
}
