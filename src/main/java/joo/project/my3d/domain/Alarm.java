package joo.project.my3d.domain;

import joo.project.my3d.domain.auditing.AuditingFields;
import joo.project.my3d.domain.constant.AlarmType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.Persistable;

import javax.persistence.*;

@Getter
@ToString(callSuper = true)
@Table(
        name = "alarm",
        indexes = {
                @Index(columnList = "id")
        }
)
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Alarm extends AuditingFields implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlarmType alarmType;

    @Column(nullable = false)
    private String fromUserNickname; //알람을 발생시킨 유저
    @Column(nullable = false)
    private Long targetId; //게시글 id
    @Setter
    @Column(nullable = false)
    private boolean isChecked;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "userAccountId")
    private UserAccount userAccount; //알람을 받는 유저

    protected Alarm() {
    }

    private Alarm(AlarmType alarmType, String fromUserNickname, Long targetId, boolean isChecked, UserAccount userAccount) {
        this.alarmType = alarmType;
        this.fromUserNickname = fromUserNickname;
        this.targetId = targetId;
        this.isChecked = isChecked;
        this.userAccount = userAccount;
    }

    public static Alarm of(AlarmType alarmType, String fromUserNickname, Long targetId, boolean isChecked, UserAccount userAccount) {
        return new Alarm(alarmType, fromUserNickname, targetId, isChecked, userAccount);
    }

    @Override
    public boolean isNew() {
        return this.createdAt == null;
    }
}
