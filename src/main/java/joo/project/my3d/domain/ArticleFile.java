package joo.project.my3d.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

import static javax.persistence.FetchType.LAZY;

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
    @OneToOne(mappedBy = "articleFile")
    private Article article;

    @ToString.Exclude
    @Setter
    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "dimension_option_id")
    private DimensionOption dimensionOption;

    protected ArticleFile() {
    }

    private ArticleFile(Long byteSize, String originalFileName, String fileName, String fileExtension, DimensionOption dimensionOption) {
        this.byteSize = byteSize;
        this.originalFileName = originalFileName;
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.dimensionOption = dimensionOption;
    }

    public static ArticleFile of(Long byteSize, String originalFileName, String fileName, String fileExtension, DimensionOption dimensionOption) {
        return new ArticleFile(byteSize, originalFileName, fileName, fileExtension, dimensionOption);
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

    public void update(Long byteSize, String originalFileName, String fileName, String fileExtension) {
        this.byteSize = byteSize;
        this.originalFileName = originalFileName;
        this.fileName = fileName;
        this.fileExtension = fileExtension;
    }
}
