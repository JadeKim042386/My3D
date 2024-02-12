package joo.project.my3d.repository;

import joo.project.my3d.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByUserAccount_Email(String email);

    boolean existsByCompanyName(String companyName);
}
