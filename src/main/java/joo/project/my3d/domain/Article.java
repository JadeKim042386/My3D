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

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private UserAccount userAccount;

    @Setter
    @Column(nullable = false)
    private String title;
    @Setter
    @Column(nullable = false)
    private String content;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArticleType articleType;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column
    private ArticleCategory articleCategory; //"ArticleType=MODEL"일 경우 non-null

    @ToString.Exclude
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "article")
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "article")
    private final Set<ArticleLike> articleLikes = new LinkedHashSet<>();

    protected Article() {
    }

    private Article(UserAccount userAccount, String title, String content, ArticleType articleType, ArticleCategory articleCategory) {
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
        this.articleType = articleType;
        this.articleCategory = articleCategory;
    }

    public static Article of(UserAccount userAccount, String title, String content, ArticleType articleType, ArticleCategory articleCategory) {
        return new Article(userAccount, title, content, articleType, articleCategory);
    }

    public static Article of(UserAccount userAccount, String title, String content, ArticleType articleType) {
        return Article.of(userAccount, title, content, articleType, null);
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
}
