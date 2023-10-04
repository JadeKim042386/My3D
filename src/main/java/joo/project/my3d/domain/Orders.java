package joo.project.my3d.domain;

import joo.project.my3d.domain.constant.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString(callSuper = true)
@Table(
        name = "orders",
        indexes = {
                @Index(columnList = "id")
        }
)
@Entity
public class Orders extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    @Embedded
    private Address address;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "userAccountId")
    private UserAccount userAccount;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "companyId")
    private Company company;

    protected Orders() {
    }

    private Orders(OrderStatus status, String productName, Address address, UserAccount userAccount, Company company) {
        this.status = status;
        this.productName = productName;
        this.address = address;
        this.userAccount = userAccount;
        this.company = company;
    }

    public static Orders of(OrderStatus status, String productName, Address address, UserAccount userAccount, Company company) {
        return new Orders(status, productName, address, userAccount, company);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Orders that)) return false;
        return this.getId() != null && Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
