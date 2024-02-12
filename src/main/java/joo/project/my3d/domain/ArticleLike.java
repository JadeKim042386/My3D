package joo.project.my3d.domain;

import joo.project.my3d.domain.auditing.AuditingFields;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@Getter
@ToString(callSuper = true)
@Table(
        name = "article_like",
        indexes = {@Index(columnList = "id"), @Index(columnList = "userAccountId, articleId")})
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ArticleLike extends AuditingFields implements Persistable<Long> {
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

    protected ArticleLike() {}

    private ArticleLike(UserAccount userAccount, Article article) {
        this.userAccount = userAccount;
        this.article = article;
    }

    public static ArticleLike of(UserAccount userAccount, Article article) {
        return new ArticleLike(userAccount, article);
    }

    @Override
    public boolean isNew() {
        return this.createdAt == null;
    }
}
