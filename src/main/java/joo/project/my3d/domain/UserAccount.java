package joo.project.my3d.domain;

import joo.project.my3d.domain.constant.UserRole;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
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
public class UserAccount extends AuditingFields implements Persistable<Long> {
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
    @Column
    //TODO: 더 이상 필요없을 것 같으니 확인 후 삭제
    private boolean signUp = false; //회원가입 여부

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

    protected UserAccount() {
    }

    private UserAccount(String email, String userPassword, String nickname, String phone, Address address, boolean signUp, UserRole userRole, UserRefreshToken userRefreshToken, Company company, String createdBy) {
        this.email = email;
        this.userPassword = userPassword;
        this.nickname = nickname;
        this.phone = phone;
        this.address = address;
        this.signUp = signUp;
        this.userRole = userRole;
        this.userRefreshToken = userRefreshToken;
        this.company = company;
        this.createdBy = createdBy;
        this.modifiedBy = createdBy;
    }

    /**
     * 회원 저장(saveUser)시 사용<br>
     * 폰번호, 주소, 기업 제외
     */
    public static UserAccount of(String email, String userPassword, String nickname, boolean signUp, UserRole userRole, UserRefreshToken userRefreshToken, String createdBy) {
        return new UserAccount(
                email,
                userPassword,
                nickname,
                null,
                Address.of(null, null, null),
                signUp,
                userRole,
                userRefreshToken,
                Company.of(null, null),
                createdBy
        );
    }

    /**
     * DTO 를 Entity 로 변환시 사용 <br>
     * 생성자(createdBy) 제외
     */
    public static UserAccount of(String email, String userPassword, String nickname, String phone, Address address, boolean signUp, UserRole userRole, UserRefreshToken userRefreshToken, Company company) {
        return new UserAccount(
                email,
                userPassword,
                nickname,
                phone,
                address,
                signUp,
                userRole,
                userRefreshToken,
                company,
                null
        );
    }

    /**
     * 모든 필드 주입
     */
    public static UserAccount of(String email, String userPassword, String nickname, String phone, Address address, boolean signUp, UserRole userRole, UserRefreshToken userRefreshToken, Company company, String createdBy) {
        return new UserAccount(
                email,
                userPassword,
                nickname,
                phone,
                address,
                signUp,
                userRole,
                userRefreshToken,
                company,
                createdBy
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
