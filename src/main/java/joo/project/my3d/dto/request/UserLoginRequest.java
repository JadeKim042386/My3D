package joo.project.my3d.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

public record UserLoginRequest(@NotBlank @Email String email, @NotBlank String password) {}
