package joo.project.my3d.domain;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(callSuper = true)
@Table(
        name = "user_account",
        indexes = {
                @Index(columnList = "userId"),
                @Index(columnList = "email", unique = true)
        }
)
@Entity
public class UserAccount extends AuditingFields {
    @Id
    @Column(length = 50)
    private String userId;

    @Column(nullable = false)
    private String userPassword;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String nickname;

    @ToString.Exclude
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "userAccount")
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();

    @ToString.Exclude
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "userAccount")
    private final Set<Article> articles = new LinkedHashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "userAccount")
    private final Set<Like> likes = new LinkedHashSet<>();

    protected UserAccount() {
    }

    private UserAccount(String userId, String userPassword, String email, String nickname) {
        this.userId = userId;
        this.userPassword = userPassword;
        this.email = email;
        this.nickname = nickname;
    }

    public static UserAccount of(String userId, String password, String email, String nickname) {
        return new UserAccount(userId, password, email, nickname);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccount that)) return false;
        return this.getUserId() != null && Objects.equals(this.getUserId(), that.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getUserId());
    }
}
