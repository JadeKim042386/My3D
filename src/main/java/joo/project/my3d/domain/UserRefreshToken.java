package joo.project.my3d.domain;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(
        name = "user_refresh_token",
        indexes = {@Index(name = "user_account_id_and_reissue_count_idx", columnList = "userAccountId, reissueCount")})
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserRefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userAccountId")
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
}
