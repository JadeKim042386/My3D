package joo.project.my3d.service;

import joo.project.my3d.domain.DimensionOption;
import joo.project.my3d.dto.DimensionOptionDto;
import joo.project.my3d.exception.DimensionOptionException;
import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.repository.DimensionOptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DimensionOptionService {

    private final DimensionOptionRepository dimensionOptionRepository;

    /**
     * @throws DimensionOptionException 상품 옵션 저장 실패 예외
     */
    @Transactional
    public DimensionOption saveDimensionOption(DimensionOptionDto dto) {
        try{
            DimensionOption dimensionOption = dto.toEntity();
            return dimensionOptionRepository.save(dimensionOption);
        } catch (IllegalArgumentException e) {
            throw new DimensionOptionException(ErrorCode.FAILED_SAVE, e);
        } catch (OptimisticLockingFailureException e) {
            throw new DimensionOptionException(ErrorCode.CONFLICT_SAVE, e);
        }
    }

    /**
     * @throws DimensionOptionException 치수 옵션을 찾을 수 없을 경우 발생하는 예외
     */
    @Transactional
    public void updateDimensionOption(DimensionOptionDto dto) {
        try {
            DimensionOption dimensionOption = dimensionOptionRepository.getReferenceById(dto.id());
            if (dto.optionName() != null) {
                dimensionOption.setOptionName(dto.optionName());
            }
        } catch (EntityNotFoundException e) {
            throw new DimensionOptionException(ErrorCode.DIMENSION_OPTION_NOT_FOUND, e);
        }
    }
}
