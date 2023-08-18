package joo.project.my3d.domain;

import joo.project.my3d.domain.constant.UserRole;
import lombok.Getter;
import lombok.Setter;
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
                @Index(columnList = "email", unique = true),
                @Index(columnList = "nickname", unique = true)
        }
)
@Entity
public class UserAccount extends AuditingFields {
    @Id
    @Column(length = 100)
    private String email;

    @Setter
    @Column(nullable = false)
    private String userPassword;

    @Setter
    @Column(nullable = false)
    private String nickname;

    @Setter
    @Column(length = 11)
    private String phone;

    @Setter
    @Column
    @Embedded
    private Address address;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @ToString.Exclude
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL)
    private final Set<Article> articles = new LinkedHashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "userAccount")
    private final Set<ArticleLike> articleLikes = new LinkedHashSet<>();

    @ToString.Exclude
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "userAccount")
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();

    protected UserAccount() {
    }

    private UserAccount(String email, String userPassword, String nickname, String phone, Address address, UserRole userRole, String createdBy) {
        this.email = email;
        this.userPassword = userPassword;
        this.nickname = nickname;
        this.phone = phone;
        this.address = address;
        this.userRole = userRole;
        this.createdBy = createdBy;
        this.modifiedBy = createdBy;
    }

    public static UserAccount of(String email, String userPassword, String nickname, UserRole userRole) {
        return new UserAccount(email, userPassword, nickname, null, null, userRole, null);
    }

    public static UserAccount of(String email, String userPassword, String nickname, Address address, UserRole userRole) {
        return new UserAccount(email, userPassword, nickname, null, address, userRole, null);
    }

    public static UserAccount of(String email, String userPassword, String nickname, String phone, UserRole userRole) {
        return new UserAccount(email, userPassword, nickname, phone, null, userRole, null);
    }

    public static UserAccount of(String email, String userPassword, String nickname, String phone, Address address, UserRole userRole) {
        return new UserAccount(email, userPassword, nickname, phone, address, userRole, null);
    }

    public static UserAccount of(String email, String userPassword, String nickname, String phone, Address address, UserRole userRole, String createdBy) {
        return new UserAccount(email, userPassword, nickname, phone, address, userRole, createdBy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccount that)) return false;
        return this.getEmail() != null && Objects.equals(this.getEmail(), that.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getEmail());
    }
}
