package joo.project.my3d.repository;

import joo.project.my3d.domain.Alarm;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    @EntityGraph(attributePaths = {"sender", "article"})
    List<Alarm> findAllByReceiverId(Long receiverId);
}
