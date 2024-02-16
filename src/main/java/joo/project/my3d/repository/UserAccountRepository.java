package joo.project.my3d.repository;

import joo.project.my3d.domain.Company;
import joo.project.my3d.domain.UserAccount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    @EntityGraph(attributePaths = {"company", "userRefreshToken"})
    Optional<UserAccount> findByEmail(String email);

    @EntityGraph(attributePaths = {"company", "userRefreshToken"})
    UserAccount getReferenceByEmail(String email);

    @EntityGraph(attributePaths = {"company", "userRefreshToken"})
    UserAccount getReferenceById(Long userAccountId);

    boolean existsByEmailOrNickname(String email, String nickname);

    @Query("select ua.company from UserAccount ua where ua.id = ?1")
    Optional<Company> findCompanyById(Long userAccountId);
}
