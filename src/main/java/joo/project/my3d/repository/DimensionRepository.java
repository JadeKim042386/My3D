package joo.project.my3d.repository;

import joo.project.my3d.domain.Dimension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DimensionRepository extends JpaRepository<Dimension, Long> {
    void deleteByArticleId(Long articleId);

    List<Dimension> findByArticleId(Long articleId);
}