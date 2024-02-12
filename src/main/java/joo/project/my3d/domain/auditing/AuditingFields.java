package joo.project.my3d.domain.auditing;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Getter
@ToString
@MappedSuperclass
public abstract class AuditingFields extends AuditingAt {
    @CreatedBy
    @Column(nullable = false, length = 100, updatable = false)
    protected String createdBy; // 생성자

    @LastModifiedBy
    @Column(nullable = false, length = 100)
    protected String modifiedBy; // 수정자
}
