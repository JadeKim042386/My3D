package joo.project.my3d.dto.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class CompanyAdminRequest {
    @NotBlank
    private String companyName;

    @NotBlank
    private String homepage;
}
