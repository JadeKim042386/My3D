package joo.project.my3d.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class DateAudit {
    private String createdAt;
    private String modifiedAt;
}
