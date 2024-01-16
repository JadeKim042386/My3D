package joo.project.my3d.repository;

import joo.project.my3d.domain.Dimension;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DimensionRepository extends JpaRepository<Dimension, Long> {
}
