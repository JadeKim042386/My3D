package joo.project.my3d.service;

import joo.project.my3d.domain.Dimension;
import joo.project.my3d.domain.DimensionOption;
import joo.project.my3d.dto.DimensionDto;
import joo.project.my3d.exception.DimensionException;
import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.repository.DimensionOptionRepository;
import joo.project.my3d.repository.DimensionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DimensionService {

    private final DimensionRepository dimensionRepository;
    private final DimensionOptionRepository dimensionOptionRepository;


    public List<DimensionDto> getDimensions(Long dimensionOptionId) {
        return dimensionRepository.findByDimensionOptionId(dimensionOptionId)
                .stream().map(DimensionDto::from)
                .toList();
    }

    /**
     * @throws DimensionException 치수 저장 실패 예외
     */
    @Transactional
    public void saveDimension(DimensionDto dto) {
        try{
            DimensionOption dimensionOption = dimensionOptionRepository.getReferenceById(dto.dimensionOptionId());
            Dimension dimension = dto.toEntity(dimensionOption);
            dimensionRepository.save(dimension);
        } catch (EntityNotFoundException e) {
            throw new DimensionException(ErrorCode.FAILED_SAVE, e);
        }
    }

    @Transactional
    public void updateDimension(DimensionDto dto) {
        try {
            Dimension dimension = dimensionRepository.getReferenceById(dto.id());
            if (dto.dimName() != null) {
                dimension.setDimName(dto.dimName());
            }
            if (dto.dimValue() != null) {
                dimension.setDimValue(dto.dimValue());
            }
            if (dto.dimUnit() != null) {
                dimension.setDimUnit(dto.dimUnit());
            }
        } catch (EntityNotFoundException e) {
            log.warn("치수 수정 실패! - dto: {} {}", dto, new DimensionException(ErrorCode.DIMENSION_NOT_FOUND, e));
        }
    }

    /**
     * @throws IllegalArgumentException id가 null일 경우 발생하는 예외
     */
    @Transactional
    public void deleteDimension(Long dimensionId) {
        dimensionRepository.deleteById(dimensionId);
    }

    /**
     * @throws IllegalArgumentException 삭제시 대상 id가 null일 경우 발생하는 예외
     */
    public void deleteDimensions(Long dimensionOptionId) {
        List<Dimension> dimensions = dimensionRepository.findByDimensionOptionId(dimensionOptionId);
        for (Dimension dimension : dimensions) {
            dimensionRepository.deleteById(dimension.getId());
        }
    }
}
