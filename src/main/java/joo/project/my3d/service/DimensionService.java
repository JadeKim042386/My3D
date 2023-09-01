package joo.project.my3d.service;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.Dimension;
import joo.project.my3d.domain.GoodOption;
import joo.project.my3d.dto.DimensionDto;
import joo.project.my3d.dto.GoodOptionDto;
import joo.project.my3d.exception.CommentException;
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
    private final ArticleRepository articleRepository;


    public List<DimensionDto> getDimensions(Long articleId) {
        return dimensionRepository.findByArticleId(articleId)
                .stream().map(DimensionDto::from)
                .toList();
    }

    @Transactional
    public void saveDimension(DimensionDto dto) {
        try{
            Article article = articleRepository.getReferenceById(dto.articleId());
            Dimension dimension = dto.toEntity(article);
            dimensionRepository.save(dimension);
        } catch (EntityNotFoundException e) {
            log.warn("치수 저장 실패! - {}", new CommentException(ErrorCode.DATA_FOR_COMMENT_NOT_FOUND));
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
            log.warn("치수 수정 실패! - dto: {} {}", dto, new CommentException(ErrorCode.COMMENT_NOT_FOUND));
        }
    }

    @Transactional
    public void deleteDimension(Long dimensionId) {
        dimensionRepository.deleteById(dimensionId);
    }
}
