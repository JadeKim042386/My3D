package joo.project.my3d.dto.validation;

import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class EnumValidator implements ConstraintValidator<ValidEnum, String> {
    private ValidEnum annotation;

    @Override
    public void initialize(ValidEnum constraintAnnotation) {
        this.annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        Enum<?>[] enums = this.annotation.enumClass().getEnumConstants();
        if (enums != null) {
            for (Enum<?> anEnum : enums) {
                if (value.equalsIgnoreCase(anEnum.toString())) {
                    return true;
                }
            }
        }
        return false;
    }
}
