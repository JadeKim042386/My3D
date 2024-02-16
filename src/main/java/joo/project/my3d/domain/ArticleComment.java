package joo.project.my3d.domain;

import joo.project.my3d.domain.auditing.AuditingFields;
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
        name = "article_comment",
        indexes = {@Index(name = "comment_id_and_user_account_id_idx", columnList = "id, userAccountId")})
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ArticleComment extends AuditingFields implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
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
    private final Set<ArticleComment> childComments = new LinkedHashSet<>();

    protected ArticleComment() {}

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

    public void addChildComment(ArticleComment child) {
        child.setParentCommentId(this.getId());
        this.getChildComments().add(child);
    }

    @Override
    public boolean isNew() {
        return this.createdAt == null;
    }
}
