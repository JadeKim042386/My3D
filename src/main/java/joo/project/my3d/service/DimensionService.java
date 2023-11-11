package joo.project.my3d.service;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.Dimension;
import joo.project.my3d.domain.GoodOption;
import joo.project.my3d.dto.DimensionDto;
import joo.project.my3d.exception.CommentException;
import joo.project.my3d.exception.DimensionException;
import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.repository.ArticleRepository;
import joo.project.my3d.repository.DimensionRepository;
import joo.project.my3d.repository.GoodOptionRepository;
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
    private final GoodOptionRepository goodOptionRepository;


    public List<DimensionDto> getDimensions(Long goodOptionId) {
        return dimensionRepository.findByGoodOptionId(goodOptionId)
                .stream().map(DimensionDto::from)
                .toList();
    }

    /**
     * @throws DimensionException 치수 저장 실패 예외
     */
    @Transactional
    public void saveDimension(DimensionDto dto) {
        try{
            GoodOption goodOption = goodOptionRepository.getReferenceById(dto.goodOptionId());
            Dimension dimension = dto.toEntity(goodOption);
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

    @Transactional
    public void deleteDimension(Long dimensionId) {
        dimensionRepository.deleteById(dimensionId);
    }

    public void deleteDimensions(Long goodOptionId) {
        List<Dimension> dimensions = dimensionRepository.findByGoodOptionId(goodOptionId);
        for (Dimension dimension : dimensions) {
            dimensionRepository.deleteById(dimension.getId());
        }
    }
}
