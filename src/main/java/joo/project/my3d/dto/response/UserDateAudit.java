package joo.project.my3d.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class UserDateAudit extends DateAudit{
    private String createdBy;
    private String modifiedBy;
}
