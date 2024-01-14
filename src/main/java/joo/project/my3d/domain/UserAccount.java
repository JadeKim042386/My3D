package joo.project.my3d.domain;

import joo.project.my3d.domain.constant.UserRole;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Objects;
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
@EntityListeners(AuditingEntityListener.class)
public class UserAccount implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @OneToMany(mappedBy = "userAccount")
    private final Set<ArticleLike> articleLikes = new LinkedHashSet<>();

    @ToString.Exclude
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "userAccount")
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();

    @ToString.Exclude
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "userAccount")
    private final Set<Alarm> alarms = new LinkedHashSet<>();

    //TODO: Auditing 클래스를 따로 만드는 것이 나을 것 같음
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @CreatedDate
    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @LastModifiedDate
    @Column(nullable = false)
    protected LocalDateTime modifiedAt;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserAccount that)) return false;
        return this.getId() != null && Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    @Override
    public boolean isNew() {
        return this.createdAt == null;
    }
}
