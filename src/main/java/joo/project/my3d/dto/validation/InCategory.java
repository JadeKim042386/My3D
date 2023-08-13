package joo.project.my3d.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = InCategoryValidator.class)
public @interface InCategory {

    String message() default "다시 선택해주세요.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
