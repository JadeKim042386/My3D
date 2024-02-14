package joo.project.my3d.repository;

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

    boolean existsByEmailOrNickname(String email, String nickname);

    @Query("select ua from UserAccount ua where ua.id = (select a.userAccount.id from Article a where a.id = ?1)")
    Optional<UserAccount> findByArticleId(Long articleId);
}
