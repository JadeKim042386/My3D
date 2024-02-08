package joo.project.my3d.repository;

import joo.project.my3d.domain.UserAccount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByCompanyId(Long companyId);
    @EntityGraph(attributePaths = {"company", "userRefreshToken"})
    Optional<UserAccount> findByEmail(String email);
    @EntityGraph(attributePaths = {"company", "userRefreshToken"})
    UserAccount getReferenceByEmail(String email);
    boolean existsByEmailOrNickname(String email, String nickname);
}
