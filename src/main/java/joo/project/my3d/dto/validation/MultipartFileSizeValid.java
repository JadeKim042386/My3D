package joo.project.my3d.dto.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = MultipartFileSizeValidator.class)
public @interface MultipartFileSizeValid {

    String message() default "파일이 비어있습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
