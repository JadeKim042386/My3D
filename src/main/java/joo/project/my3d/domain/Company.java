package joo.project.my3d.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(callSuper = true)
@Table(
        name = "company",
        indexes = {
                @Index(columnList = "id")
        }
)
@Entity
public class Company extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column
    private String companyName;
    @Setter
    @Column
    private String homepage;

    @ToString.Exclude
    @OneToOne(mappedBy = "company")
    private UserAccount userAccount;

    @ToString.Exclude
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "company")
    private final Set<Orders> orders = new LinkedHashSet<>();

    protected Company() {
    }

    private Company(String companyName, String homepage) {
        this.companyName = companyName;
        this.homepage = homepage;
    }

    public static Company of(String companyName, String homepage) {
        return new Company(companyName, homepage);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Company that)) return false;
        return this.getId() != null && Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
