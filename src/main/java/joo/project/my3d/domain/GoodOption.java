package joo.project.my3d.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString(callSuper = true)
@Table(
        name = "good_option",
        indexes = {
                @Index(columnList = "id")
        }
)
@Entity
public class GoodOption extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(nullable = false)
    private String optionName; //상품명
    @Setter
    @Column(nullable = false)
    private Integer addPrice = 0; //추가금액
    @Setter
    @Column(nullable = false)
    private String printingTech; //프린팅 방식
    @Setter
    @Column(nullable = false)
    private String material; //프린팅 소재

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "articleId")
    private Article article;

    protected GoodOption() {
    }

    private GoodOption(Article article, String optionName, Integer addPrice, String printing_tech, String material) {
        this.article = article;
        this.optionName = optionName;
        this.addPrice = addPrice;
        this.printingTech = printing_tech;
        this.material = material;
    }

    public static GoodOption of(Article article, String optionName, Integer addPrice, String printing_tech, String material) {
        return new GoodOption(article, optionName, addPrice, printing_tech, material);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GoodOption that)) return false;
        return this.getId() != null && Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
