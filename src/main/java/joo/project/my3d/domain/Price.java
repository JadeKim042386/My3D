package joo.project.my3d.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString(callSuper = true)
@Table(
        name = "price",
        indexes = {
                @Index(columnList = "id")
        }
)
@Entity
public class Price extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private Integer priceValue = 0; //상품금액
    @Setter
    @Column(nullable = false)
    private Integer deliveryPrice = 0; //배송료

    @ToString.Exclude
    @OneToOne(mappedBy = "price")
    private Article article;

    protected Price() {
    }

    private Price(Integer priceValue, Integer deliveryPrice) {
        this.priceValue = priceValue;
        this.deliveryPrice = deliveryPrice;
    }

    public static Price of(Integer priceValue, Integer deliveryPrice) {
        return new Price(priceValue, deliveryPrice);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Price that)) return false;
        return this.getId() != null && Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
