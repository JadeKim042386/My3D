package joo.project.my3d.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Getter
@ToString(callSuper = true)
@Table(name = "article_file")
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ArticleFile implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private Long byteSize;

    @Column(nullable = false)
    private String originalFileName; // 사용자가 지정한 파일명

    @Column(nullable = false, unique = true)
    private String fileName; // 서버에 저장된 파일명

    @Column(nullable = false)
    private String fileExtension;

    @ToString.Exclude
    @OneToOne(mappedBy = "articleFile")
    private Article article;

    @ToString.Exclude
    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "dimensionOptionId")
    private DimensionOption dimensionOption;

    protected ArticleFile() {}

    private ArticleFile(
            Long byteSize,
            String originalFileName,
            String fileName,
            String fileExtension,
            DimensionOption dimensionOption) {
        this.byteSize = byteSize;
        this.originalFileName = originalFileName;
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.dimensionOption = dimensionOption;
    }

    public static ArticleFile of(
            Long byteSize,
            String originalFileName,
            String fileName,
            String fileExtension,
            DimensionOption dimensionOption) {
        return new ArticleFile(byteSize, originalFileName, fileName, fileExtension, dimensionOption);
    }

    public void update(Long byteSize, String originalFileName, String fileName, String fileExtension) {
        this.byteSize = byteSize;
        this.originalFileName = originalFileName;
        this.fileName = fileName;
        this.fileExtension = fileExtension;
    }

    @Override
    public boolean isNew() {
        return this.id == null;
    }
}
