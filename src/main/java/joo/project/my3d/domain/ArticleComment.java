package joo.project.my3d.domain;

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
        name = "article_comment",
        indexes = {
                @Index(columnList = "id"),
                @Index(columnList = "articleId")
        }
)
@Entity
public class ArticleComment extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "userAccountId")
    private UserAccount userAccount;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "articleId")
    private Article article;

    @Setter
    @Column(nullable = false)
    private String content;

    @Setter
    @Column(updatable = false)
    private Long parentCommentId;

    @ToString.Exclude
    @OrderBy("createdAt ASC")
    @OneToMany(mappedBy = "parentCommentId", cascade = CascadeType.ALL)
    private Set<ArticleComment> childComments = new LinkedHashSet<>();

    protected ArticleComment() {
    }

    private ArticleComment(UserAccount userAccount, Article article, String content, Long parentCommentId) {
        this.userAccount = userAccount;
        this.article = article;
        this.content = content;
        this.parentCommentId = parentCommentId;
    }

    public static ArticleComment of(UserAccount userAccount, Article article, String content, Long parentCommentId) {
        return new ArticleComment(userAccount, article, content, parentCommentId);
    }

    public static ArticleComment of(UserAccount userAccount, Article article, String content) {
        return new ArticleComment(userAccount, article, content, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleComment that)) return false;
        return this.getId() != null && Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }

    public void addChildComment(ArticleComment child) {
        child.setParentCommentId(this.getId());
        this.getChildComments().add(child);
    }
}
