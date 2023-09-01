package joo.project.my3d.repository;

import joo.project.my3d.domain.Price;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PriceRepository extends JpaRepository<Price, Long> {
    void deleteByArticleId(Long articleId);
}
