package joo.project.my3d.repository;

import joo.project.my3d.domain.UserRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRefreshTokenRepository extends JpaRepository<UserRefreshToken, Long> {
    Optional<UserRefreshToken> findByUserAccountIdAndReissueCountLessThan(long id, long count);
}
