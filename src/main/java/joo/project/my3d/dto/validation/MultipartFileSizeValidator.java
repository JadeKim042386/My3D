package joo.project.my3d.dto.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;

@Component
public class MultipartFileSizeValidator implements ConstraintValidator<MultipartFileSizeValid, MultipartFile> {
    private static final long FILE_SIZE = 0L;

    @Override
    public boolean isValid(final MultipartFile file, final ConstraintValidatorContext context) {

        return file.getSize() > FILE_SIZE;
    }
}
