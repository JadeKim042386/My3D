package joo.project.my3d.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@Getter
@Entity
@Table(
        name = "user_refresh_token",
        indexes = {@Index(name = "reissue_count_idx", columnList = "reissueCount")})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserRefreshToken implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @OneToOne(mappedBy = "userRefreshToken", cascade = CascadeType.ALL)
    private UserAccount userAccount;

    private String refreshToken;
    private long reissueCount = 0;

    protected UserRefreshToken() {}

    private UserRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public static UserRefreshToken of(String refreshToken) {
        return new UserRefreshToken(refreshToken);
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public boolean equalRefreshToken(String refreshToken) {
        return this.refreshToken.equals(refreshToken);
    }

    public void increaseReissueCount() {
        this.reissueCount++;
    }

    @Override
    public boolean isNew() {
        return this.id == null;
    }
}
