package joo.project.my3d.domain;

import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserRefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;
    private String refreshToken;
    private int reissueCount = 0;

    protected UserRefreshToken() {
    }

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
