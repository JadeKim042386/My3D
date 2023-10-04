package joo.project.my3d.domain;

import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static javax.persistence.FetchType.LAZY;

@Getter
@ToString(callSuper = true)
@Table(
        name = "article",
        indexes = {
                @Index(columnList = "id")
        }
)
@Entity
public class Article extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "userAccountId")
    private UserAccount userAccount;

    @Setter
    @Column(nullable = false)
    private String title;
    @Setter
    @Column(nullable = false)
    private String summary; //요약 내용
    @Setter
    @Column(nullable = false)
    private String content; //본문

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArticleType articleType;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column
    private ArticleCategory articleCategory; //"ArticleType=MODEL"일 경우 non-null

    @Setter
    @Column(nullable = false)
    private int likeCount = 0;

    @ToString.Exclude
    @OneToMany(mappedBy = "article", fetch = LAZY, cascade = CascadeType.ALL)
    private final Set<ArticleFile> articleFiles = new LinkedHashSet<>();

    @ToString.Exclude
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "article")
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "article")
    private final Set<ArticleLike> articleLikes = new LinkedHashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "article", fetch = LAZY, cascade = CascadeType.ALL)
    private final Set<GoodOption> goodOptions = new LinkedHashSet<>();

    @ToString.Exclude
    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "price_id")
    private Price price;

    protected Article() {
    }

    private Article(UserAccount userAccount, String title, String summary, String content, ArticleType articleType, ArticleCategory articleCategory, Price price) {
        this.userAccount = userAccount;
        this.title = title;
        this.summary = summary;
        this.content = content;
        this.articleType = articleType;
        this.articleCategory = articleCategory;
        this.price = price;
    }

    public static Article of(UserAccount userAccount, String title, String summary, String content, ArticleType articleType, ArticleCategory articleCategory, Price price) {
        return new Article(userAccount, title, summary, content, articleType, articleCategory, price);
    }

    public static Article of(UserAccount userAccount, String title, String summary, String content, ArticleType articleType, Price price) {
        return Article.of(userAccount, title, summary, content, articleType, null, price);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article that)) return false;
        return this.getId() != null && Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    public void addLike() {
        this.likeCount += 1;
    }

    public void deleteLike() {
        this.likeCount -= 1;
    }
}
