package joo.project.my3d.domain;

import joo.project.my3d.domain.auditing.AuditingFields;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static javax.persistence.FetchType.LAZY;

@Getter
@ToString(callSuper = true)
@Table(name = "dimension_option")
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DimensionOption implements Persistable<Long> {
    @ToString.Exclude
    @OneToMany(mappedBy = "dimensionOption", fetch = LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<Dimension> dimensions = new LinkedHashSet<>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Setter
    @Column(nullable = false)
    private String optionName; // 치수명

    @ToString.Exclude
    @OneToOne(mappedBy = "dimensionOption")
    private ArticleFile articleFile;

    protected DimensionOption() {}

    private DimensionOption(String optionName) {
        this.optionName = optionName;
    }

    public static DimensionOption of(String optionName) {
        return new DimensionOption(optionName);
    }

    @Override
    public boolean isNew() {
        return this.id == null;
    }

    public void updateDimensions(Collection<Dimension> dimensions) {
        this.dimensions.clear();
        this.dimensions.addAll(dimensions);
    }
}
