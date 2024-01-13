package joo.project.my3d.domain;

import javax.persistence.*;

@Entity
public class UserRefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
