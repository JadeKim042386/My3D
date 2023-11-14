package joo.project.my3d.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import static javax.persistence.FetchType.LAZY;

@Getter
@ToString(callSuper = true)
@Table(
        name = "dimension_option",
        indexes = {
                @Index(columnList = "id")
        }
)
@Entity
public class DimensionOption extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String optionName; //치수명

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "articleId")
    private Article article;

    @ToString.Exclude
    @OneToMany(mappedBy = "dimensionOption", fetch = LAZY, cascade = CascadeType.ALL)
    private final Set<Dimension> dimensions = new LinkedHashSet<>();

    protected DimensionOption() {
    }

    private DimensionOption(Article article, String optionName) {
        this.article = article;
        this.optionName = optionName;
    }

    public static DimensionOption of(Article article, String optionName) {
        return new DimensionOption(article, optionName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DimensionOption that)) return false;
        return this.getId() != null && Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
