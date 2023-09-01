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
                @Index(columnList = "id"),
                @Index(columnList = "fileName", unique = true)
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
    private String originalFileName;
    @Column(nullable = false)
    private String fileName;
    @Column(nullable = false)
    private String fileExtension;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "articleId")
    private Article article;

    protected ArticleFile() {
    }

    private ArticleFile(Article article, Long byteSize, String originalFileName, String fileName, String fileExtension) {
        this.article = article;
        this.byteSize = byteSize;
        this.originalFileName = originalFileName;
        this.fileName = fileName;
        this.fileExtension = fileExtension;
    }

    public static ArticleFile of(Article article, Long byteSize, String originalFileName, String fileName, String fileExtension) {
        return new ArticleFile(article, byteSize, originalFileName, fileName, fileExtension);
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
