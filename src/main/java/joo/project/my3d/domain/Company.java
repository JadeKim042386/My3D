package joo.project.my3d.domain;

import joo.project.my3d.domain.auditing.AuditingAt;
import joo.project.my3d.domain.auditing.AuditingFields;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@Getter
@ToString(callSuper = true)
@Table(
        name = "company",
        indexes = {@Index(name = "company_name_idx", columnList = "companyName")})
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Company implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Setter
    private String companyName;

    @Setter
    private String homepage;

    @ToString.Exclude
    @OneToOne(mappedBy = "company")
    private UserAccount userAccount;

    protected Company() {}

    private Company(String companyName, String homepage) {
        this.companyName = companyName;
        this.homepage = homepage;
    }

    public static Company of(String companyName, String homepage) {
        return new Company(companyName, homepage);
    }

    @Override
    public boolean isNew() {
        return this.id == null;
    }
}
