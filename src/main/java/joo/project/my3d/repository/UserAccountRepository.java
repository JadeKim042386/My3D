package joo.project.my3d.repository;

import joo.project.my3d.domain.UserAccount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

import static org.springframework.data.jpa.repository.EntityGraph.EntityGraphType.LOAD;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    @EntityGraph(attributePaths = "company", type = LOAD)
    Optional<UserAccount> findByEmail(String email);

    @EntityGraph(attributePaths = "company", type = LOAD)
    Optional<UserAccount> findById(Long userAccountId);

    boolean existsByEmailOrNickname(String email, String nickname);
}
