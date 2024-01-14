package joo.project.my3d.domain;

import joo.project.my3d.domain.auditing.AuditingFields;
import joo.project.my3d.domain.constant.DimUnit;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Persistable;

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
public class Dimension extends AuditingFields implements Persistable<Long> {
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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dimensionOptionId")
    private DimensionOption dimensionOption;

    protected Dimension() {
    }

    private Dimension(DimensionOption dimensionOption, String dimName, Float dimValue, DimUnit dimUnit) {
        this.dimensionOption = dimensionOption;
        this.dimName = dimName;
        this.dimValue = dimValue;
        this.dimUnit = dimUnit;
    }

    public static Dimension of(DimensionOption dimensionOption, String dimName, Float dimValue, DimUnit dimUnit) {
        return new Dimension(dimensionOption, dimName, dimValue, dimUnit);
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

    @Override
    public boolean isNew() {
        return this.createdAt == null;
    }
}
