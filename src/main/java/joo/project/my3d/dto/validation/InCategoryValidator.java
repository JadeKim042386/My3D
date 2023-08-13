package joo.project.my3d.dto.validation;

import joo.project.my3d.domain.constant.ArticleCategory;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class InCategoryValidator implements ConstraintValidator<InCategory, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        try {
            ArticleCategory.valueOf(value);
            return true;
        } catch (IllegalArgumentException e) {
            log.warn("카테고리 선택이 잘못되었습니다. - value={}", value);
            return false;
        }
    }
}
