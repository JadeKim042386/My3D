package joo.project.my3d.domain;

import joo.project.my3d.domain.constant.DimUnit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@Getter
@ToString(callSuper = true)
@Table(name = "dimension")
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Dimension implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
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

    protected Dimension() {}

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
    public boolean isNew() {
        return this.id == null;
    }
}
