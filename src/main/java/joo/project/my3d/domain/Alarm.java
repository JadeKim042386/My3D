package joo.project.my3d.domain;

import joo.project.my3d.domain.constant.AlarmType;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString(callSuper = true)
@Table(
        name = "alarm",
        indexes = {
                @Index(columnList = "id")
        }
)
@Entity
public class Alarm extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlarmType alarmType;

    @Column(nullable = false)
    private String fromUserEmail; //알람을 발생시킨 유저
    @Column(nullable = false)
    private Long targetId; //게시글, 기업 엔티티의 id

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "email")
    private UserAccount userAccount; //알람을 받는 유저

    protected Alarm() {
    }

    private Alarm(AlarmType alarmType, String fromUserEmail, Long targetId, UserAccount userAccount) {
        this.alarmType = alarmType;
        this.fromUserEmail = fromUserEmail;
        this.targetId = targetId;
        this.userAccount = userAccount;
    }

    public static Alarm of(AlarmType alarmType, String  fromUserEmail, Long targetId, UserAccount userAccount) {
        return new Alarm(alarmType, fromUserEmail, targetId, userAccount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Alarm that)) return false;
        return this.getId() != null && Objects.equals(this.getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
