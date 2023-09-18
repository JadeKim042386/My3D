package joo.project.my3d.repository;

import joo.project.my3d.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByUserAccount_Email(String email);
    List<Orders> findByCompanyId(Long companyId);
}
