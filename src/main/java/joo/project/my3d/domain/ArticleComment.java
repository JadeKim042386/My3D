package joo.project.my3d.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString(callSuper = true)
@Table(
        name = "article_comment",
        indexes = {
                @Index(columnList = "id")
        }
)
@Entity
public class ArticleComment extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "articleId")
    private Article article;

    @Setter
    @Column(nullable = false)
    private String content;

    @Column(updatable = false)
    private Long parentCommentId;

    protected ArticleComment() {
    }

    private ArticleComment(Article article, String content, Long parentCommentId) {
        this.article = article;
        this.content = content;
        this.parentCommentId = parentCommentId;
    }

    public static ArticleComment of(Article article, String content, Long parentCommentId) {
        return new ArticleComment(article, content, parentCommentId);
    }

    public static ArticleComment of(Article article, String content) {
        return new ArticleComment(article, content, null);
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
}
