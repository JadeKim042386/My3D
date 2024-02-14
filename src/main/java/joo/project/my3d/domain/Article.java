package joo.project.my3d.domain;

import joo.project.my3d.domain.auditing.AuditingFields;
import joo.project.my3d.domain.constant.ArticleCategory;
import joo.project.my3d.domain.constant.ArticleType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
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
        })
@NamedEntityGraph(
        name = "Article.fetchForm",
        attributeNodes = {
            @NamedAttributeNode(value = "userAccount", subgraph = "company"),
            @NamedAttributeNode(value = "articleFile", subgraph = "dimensionOption"),
        },
        subgraphs = {
            @NamedSubgraph(name = "company", attributeNodes = @NamedAttributeNode("company")),
            @NamedSubgraph(name = "dimensionOption", attributeNodes = @NamedAttributeNode(value = "dimensionOption"))
        })
@NamedEntityGraph(
        name = "Article.fetchPreview",
        attributeNodes = {
            @NamedAttributeNode(value = "userAccount", subgraph = "company"),
            @NamedAttributeNode(value = "articleFile"),
        },
        subgraphs = {
            @NamedSubgraph(name = "company", attributeNodes = @NamedAttributeNode("company")),
        })
@Getter
@ToString(callSuper = true)
@Table(
        name = "article",
        indexes = {@Index(columnList = "id")})
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Article extends AuditingFields implements Persistable<Long> {
    @ToString.Exclude
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "article", orphanRemoval = true)
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "article", orphanRemoval = true)
    private final Set<ArticleLike> articleLikes = new LinkedHashSet<>();

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

    @Column(nullable = false)
    private Integer likeCount = 0;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArticleType articleType;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column
    private ArticleCategory articleCategory; // "ArticleType=MODEL"일 경우 non-null

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "articleFileId")
    private ArticleFile articleFile;

    protected Article() {}

    private Article(
            UserAccount userAccount,
            ArticleFile articleFile,
            String title,
            String content,
            ArticleType articleType,
            ArticleCategory articleCategory) {
        this.userAccount = userAccount;
        this.articleFile = articleFile;
        this.title = title;
        this.content = content;
        this.articleType = articleType;
        this.articleCategory = articleCategory;
    }

    public static Article of(
            UserAccount userAccount,
            ArticleFile articleFile,
            String title,
            String content,
            ArticleType articleType,
            ArticleCategory articleCategory) {
        return new Article(userAccount, articleFile, title, content, articleType, articleCategory);
    }

    public static Article of(
            UserAccount userAccount, ArticleFile articleFile, String title, String content, ArticleType articleType) {
        return Article.of(userAccount, articleFile, title, content, articleType, null);
    }

    @Override
    public boolean isNew() {
        return this.createdAt == null;
    }

    public void deleteAll() {
        this.articleComments.clear();
        this.articleLikes.clear();
    }
}
