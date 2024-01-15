package joo.project.my3d.domain;

import joo.project.my3d.domain.auditing.AuditingFields;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@NamedEntityGraph(
        name = "Article.fetchDetail",
        attributeNodes = {
                @NamedAttributeNode(value = "userAccount", subgraph = "company"),
                @NamedAttributeNode(value = "articleFile", subgraph = "dimensionOption"),
                @NamedAttributeNode(value = "articleComments")
        },
        subgraphs = {
                @NamedSubgraph(name = "company", attributeNodes = @NamedAttributeNode("company")),
                @NamedSubgraph(name = "dimensionOption", attributeNodes = @NamedAttributeNode(value = "dimensionOption"))
        }
)
@NamedEntityGraph(
        name = "Article.fetchForm",
        attributeNodes = {
                @NamedAttributeNode(value = "userAccount", subgraph = "company"),
                @NamedAttributeNode(value = "articleFile", subgraph = "dimensionOption"),
        },
        subgraphs = {
                @NamedSubgraph(name = "company", attributeNodes = @NamedAttributeNode("company")),
                @NamedSubgraph(name = "dimensionOption", attributeNodes = @NamedAttributeNode(value = "dimensionOption"))
        }
)
@NamedEntityGraph(
        name = "Article.fetchIndex",
        attributeNodes = {
                @NamedAttributeNode(value = "userAccount", subgraph = "company"),
                @NamedAttributeNode(value = "articleFile"),
        },
        subgraphs = {
                @NamedSubgraph(name = "company", attributeNodes = @NamedAttributeNode("company")),
        }
)
@Getter
@ToString(callSuper = true)
@Table(
        name = "article",
        indexes = {
                @Index(columnList = "id")
        }
)
@Entity
public class Article extends AuditingFields implements Persistable<Long> {
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
    @Setter
    @OneToOne(mappedBy = "article", cascade = CascadeType.ALL)
    private ArticleFile articleFile;

    @ToString.Exclude
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "article")
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "article")
    private final Set<ArticleLike> articleLikes = new LinkedHashSet<>();

    protected Article() {
    }

    private Article(UserAccount userAccount, String title, String content, ArticleType articleType, ArticleCategory articleCategory, ArticleFile articleFile) {
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
        this.articleType = articleType;
        this.articleCategory = articleCategory;
        this.articleFile = articleFile;
    }

    public static Article of(UserAccount userAccount, String title, String content, ArticleType articleType, ArticleCategory articleCategory, ArticleFile articleFile) {
        return new Article(userAccount, title, content, articleType, articleCategory, articleFile);
    }

    public static Article of(UserAccount userAccount, String title, String content, ArticleType articleType, ArticleFile articleFile) {
        return Article.of(userAccount, title, content, articleType, null, articleFile);
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

    @Override
    public boolean isNew() {
        return this.createdAt == null;
    }
}
