package joo.project.my3d.domain;

import joo.project.my3d.domain.constant.DimUnit;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString(callSuper = true)
@Table(
        name = "dimension",
        indexes = {
                @Index(columnList = "id")
        }
)
@Entity
public class Dimension extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String dimName;
    @Setter
    @Column(nullable = false)
    private Float dimValue = 0.0f;
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DimUnit dimUnit;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "articleId")
    private Article article;

    protected Dimension() {
    }

    private Dimension(Article article, String dimName, Float dimValue, DimUnit dimUnit) {
        this.article = article;
        this.dimName = dimName;
        this.dimValue = dimValue;
        this.dimUnit = dimUnit;
    }

    public static Dimension of(Article article, String dimName, Float dimValue, DimUnit dimUnit) {
        return new Dimension(article, dimName, dimValue, dimUnit);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dimension that)) return false;
        return this.getId() != null && Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
