package joo.project.my3d.service;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.DimensionOption;
import joo.project.my3d.dto.DimensionOptionDto;
import joo.project.my3d.dto.DimensionOptionWithDimensionDto;
import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.exception.DimensionOptionException;
import joo.project.my3d.repository.ArticleRepository;
import joo.project.my3d.repository.DimensionOptionRepository;
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
public class DimensionOptionService {

    private final DimensionOptionRepository dimensionOptionRepository;
    private final ArticleRepository articleRepository;

    public List<DimensionOptionDto> getDimensionOptions(Long articleId) {
        return dimensionOptionRepository.findByArticleId(articleId)
                .stream().map(DimensionOptionDto::from)
                .toList();
    }

    public List<DimensionOptionWithDimensionDto> getDimensionOptionWithDimensions(Long articleId) {
        return dimensionOptionRepository.findByArticleId(articleId)
                .stream().map(DimensionOptionWithDimensionDto::from)
                .toList();
    }

    /**
     * @throws DimensionOptionException 상품 옵션 저장 실패 예외
     */
    @Transactional
    public DimensionOption saveDimensionOption(DimensionOptionDto dto) {
        try{
            Article article = articleRepository.getReferenceById(dto.articleId());
            DimensionOption dimensionOption = dto.toEntity(article);
            return dimensionOptionRepository.save(dimensionOption);
        } catch (EntityNotFoundException e) {
            throw new DimensionOptionException(ErrorCode.FAILED_SAVE, e);
        }
    }

    @Transactional
    public void updateDimensionOption(DimensionOptionDto dto) {
        try {
            DimensionOption dimensionOption = dimensionOptionRepository.getReferenceById(dto.id());
            if (dto.optionName() != null) {
                dimensionOption.setOptionName(dto.optionName());
            }
        } catch (EntityNotFoundException e) {
            log.warn("상품옵션 수정 실패! - dto: {} {}", dto, new DimensionOptionException(ErrorCode.DIMENSION_OPTION_NOT_FOUND, e));
        }
    }

    /**
     * @throws IllegalArgumentException id가 null일 경우 발생하는 예외
     */
    @Transactional
    public void deleteDimensionOption(Long dimensionOptionId) {
        dimensionOptionRepository.deleteById(dimensionOptionId);
    }

    /**
     * @throws IllegalArgumentException 삭제시 대상 id가 null일 경우 발생하는 예외
     */
    @Transactional
    public void deleteDimensionOptions(Long articleId) {
        List<DimensionOption> dimensionOptions = dimensionOptionRepository.findByArticleId(articleId);
        for (DimensionOption dimensionOption : dimensionOptions) {
            dimensionOptionRepository.deleteById(dimensionOption.getId());
        }
    }
}
