package joo.project.my3d.domain;

import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;

import java.util.Objects;


@Getter
@ToString(callSuper = true)
@Table(
        name = "article_file",
        indexes = {
                @Index(columnList = "id")
        }
)
@Entity
public class ArticleFile extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long byteSize;
    @Column(nullable = false)
    private String fileName;
    @Column(nullable = false)
    private String fileExtension;

    @ToString.Exclude
    @OneToOne(mappedBy = "article_file", fetch = FetchType.LAZY, optional = false)
    private Article article;

    protected ArticleFile() {
    }

    private ArticleFile(Long byteSize, String fileName, String fileExtension, Article article) {
        this.byteSize = byteSize;
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.article = article;
    }

    public static ArticleFile of(Long byteSize, String fileName, String fileExtension, Article article) {
        return new ArticleFile(byteSize, fileName, fileExtension, article);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleFile that)) return false;
        return this.getId() != null && Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
