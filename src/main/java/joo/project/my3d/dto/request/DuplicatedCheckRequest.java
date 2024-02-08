package joo.project.my3d.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@Setter
@NoArgsConstructor
public class DuplicatedCheckRequest {
    @Email
    private String email;
    private String nickname;
    private String companyName;
}
