package joo.project.my3d.repository;

import joo.project.my3d.domain.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByCompanyId(Long companyId);
    Optional<UserAccount> findByEmail(String email);
    UserAccount getReferenceByEmail(String email);
}
