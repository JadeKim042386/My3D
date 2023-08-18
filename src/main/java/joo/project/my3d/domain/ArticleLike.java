package joo.project.my3d.domain;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString(callSuper = true)
@Table(
        name = "article_like",
        indexes = {
                @Index(columnList = "id")
        }
)
@Entity
public class ArticleLike extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "email")
    private UserAccount userAccount;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "articleId")
    private Article article;

    protected ArticleLike() {
    }

    private ArticleLike(UserAccount userAccount, Article article) {
        this.userAccount = userAccount;
        this.article = article;
    }

    public static ArticleLike of(UserAccount userAccount, Article article) {
        return new ArticleLike(userAccount, article);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleLike that)) return false;
        return this.getId() != null && Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
