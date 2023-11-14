package joo.project.my3d.repository;

import joo.project.my3d.domain.DimensionOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DimensionOptionRepository extends JpaRepository<DimensionOption, Long> {
    void deleteByArticleId(Long articleId);

    List<DimensionOption> findByArticleId(Long articleId);
}
