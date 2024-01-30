package joo.project.my3d.domain;

import joo.project.my3d.domain.auditing.AuditingFields;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JoinColumnOrFormula;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

@NamedEntityGraph(
        name = "Article.fetchDetail",
        attributeNodes = {
                @NamedAttributeNode(value = "userAccount", subgraph = "company"),
                @NamedAttributeNode(value = "articleFile", subgraph = "dimensionOption"),
                @NamedAttributeNode(value = "articleComments"),
                @NamedAttributeNode(value = "articleLikes")
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
        name = "Article.fetchPreview",
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Article extends AuditingFields implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
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
    private Integer likeCount;

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
    @OneToMany(mappedBy = "article", orphanRemoval = true)
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "article", orphanRemoval = true)
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
    public boolean isNew() {
        return this.createdAt == null;
    }

    public void deleteAll() {
        this.articleComments.clear();
        this.articleLikes.clear();
    }

    public void updateLikeCount(boolean isAdd) {
        if (isAdd) likeCount++;
        else likeCount--;
    }
}
