package joo.project.my3d.domain;

import joo.project.my3d.domain.auditing.AuditingAt;
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
        indexes = {@Index(name = "receiver_idx", columnList = "receiverId")})
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Alarm extends AuditingAt implements Persistable<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlarmType alarmType;

    @Column(nullable = false)
    private Long targetId; // 댓글 id

    @Setter
    @Column(nullable = false)
    private boolean isChecked;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "articleId")
    private Article article;

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "senderId")
    private UserAccount sender; // 알람을 받는 유저

    @ToString.Exclude
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "receiverId")
    private UserAccount receiver; // 알람을 받는 유저

    protected Alarm() {}

    private Alarm(
            AlarmType alarmType,
            Long targetId,
            boolean isChecked,
            Article article,
            UserAccount sender,
            UserAccount receiver) {
        this.alarmType = alarmType;
        this.sender = sender;
        this.targetId = targetId;
        this.article = article;
        this.isChecked = isChecked;
        this.receiver = receiver;
    }

    public static Alarm of(
            AlarmType alarmType,
            Long targetId,
            boolean isChecked,
            Article article,
            UserAccount sender,
            UserAccount receiver) {
        return new Alarm(alarmType, targetId, isChecked, article, sender, receiver);
    }

    @Override
    public boolean isNew() {
        return this.id == null;
    }
}
