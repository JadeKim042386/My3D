package joo.project.my3d.repository;

import joo.project.my3d.domain.GoodOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoodOptionRepository extends JpaRepository<GoodOption, Long> {
    void deleteByArticleId(Long articleId);

    List<GoodOption> findByArticleId(Long articleId);
}
