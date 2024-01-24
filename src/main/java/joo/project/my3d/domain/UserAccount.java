package joo.project.my3d.domain;

import joo.project.my3d.domain.auditing.AuditingAt;
import joo.project.my3d.domain.constant.UserRole;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@ToString(callSuper = true)
@Table(
        name = "user_account",
        indexes = {
                @Index(columnList = "id"),
                @Index(columnList = "email", unique = true),
                @Index(columnList = "nickname", unique = true)
        }
)
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserAccount extends AuditingAt implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Setter
    @Column(length = 100, nullable = false)
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
    @OneToOne(mappedBy = "userAccount", cascade = CascadeType.ALL)
    private UserRefreshToken userRefreshToken;

    @ToString.Exclude
    @OneToOne(mappedBy = "userAccount", cascade = CascadeType.ALL)
    private Company company;

    @ToString.Exclude
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "userAccount", cascade = CascadeType.ALL)
    private final Set<Article> articles = new LinkedHashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "userAccount", orphanRemoval = true)
    private final Set<ArticleLike> articleLikes = new LinkedHashSet<>();

    @ToString.Exclude
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "userAccount", orphanRemoval = true)
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();

    @ToString.Exclude
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "userAccount")
    private final Set<Alarm> alarms = new LinkedHashSet<>();

    protected UserAccount() {
    }

    private UserAccount(String email, String userPassword, String nickname, String phone, Address address, UserRole userRole, UserRefreshToken userRefreshToken, Company company) {
        this.email = email;
        this.userPassword = userPassword;
        this.nickname = nickname;
        this.phone = phone;
        this.address = address;
        this.userRole = userRole;
        this.userRefreshToken = userRefreshToken;
        this.company = company;
    }

    /**
     * 회원 저장(saveUser)시 사용<br>
     * 폰번호, 주소, 기업 제외
     */
    public static UserAccount of(String email, String userPassword, String nickname, UserRole userRole, UserRefreshToken userRefreshToken) {
        return new UserAccount(
                email,
                userPassword,
                nickname,
                null,
                Address.of(null, null, null),
                userRole,
                userRefreshToken,
                Company.of(null, null)
        );
    }

    /**
     * 모든 필드 주입
     */
    public static UserAccount of(String email, String userPassword, String nickname, String phone, Address address, UserRole userRole, UserRefreshToken userRefreshToken, Company company) {
        return new UserAccount(
                email,
                userPassword,
                nickname,
                phone,
                address,
                userRole,
                userRefreshToken,
                company
        );
    }

    @Override
    public boolean isNew() {
        return this.createdAt == null;
    }
}
