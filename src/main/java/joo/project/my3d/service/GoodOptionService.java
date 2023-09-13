package joo.project.my3d.service;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.GoodOption;
import joo.project.my3d.dto.GoodOptionDto;
import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.exception.GoodOptionException;
import joo.project.my3d.repository.ArticleRepository;
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
public class GoodOptionService {

    private final GoodOptionRepository goodOptionRepository;
    private final ArticleRepository articleRepository;


    public List<GoodOptionDto> getGoodOptions(Long articleId) {
        return goodOptionRepository.findByArticleId(articleId)
                .stream().map(GoodOptionDto::from)
                .toList();
    }

    @Transactional
    public GoodOption saveGoodOption(GoodOptionDto dto) {
        try{
            Article article = articleRepository.getReferenceById(dto.articleId());
            GoodOption goodOption = dto.toEntity(article);
            return goodOptionRepository.save(goodOption);
        } catch (EntityNotFoundException e) {
            log.warn("상품옵션 저장 실패! - {}", new GoodOptionException(ErrorCode.FAILED_SAVE));
            throw new GoodOptionException(ErrorCode.FAILED_SAVE);
        }
    }

    @Transactional
    public void updateGoodOption(GoodOptionDto dto) {
        try {
            GoodOption goodOption = goodOptionRepository.getReferenceById(dto.id());
            if (dto.addPrice() != null) {
                goodOption.setAddPrice(dto.addPrice());
            }
            if (dto.optionName() != null) {
                goodOption.setOptionName(dto.optionName());
            }
            if (dto.printingTech() != null) {
                goodOption.setPrintingTech(dto.printingTech());
            }
            if (dto.material() != null) {
                goodOption.setMaterial(dto.material());
            }
        } catch (EntityNotFoundException e) {
            log.warn("상품옵션 수정 실패! - dto: {} {}", dto, new GoodOptionException(ErrorCode.GOOD_OPTION_NOT_FOUND));
        }
    }

    @Transactional
    public void deleteGoodOption(Long goodOptionId) {
        goodOptionRepository.deleteById(goodOptionId);
    }

    @Transactional
    public void deleteGoodOptions(Long articleId) {
        List<GoodOption> goodOptions = goodOptionRepository.findByArticleId(articleId);
        for (GoodOption goodOption : goodOptions) {
            goodOptionRepository.deleteById(goodOption.getId());
        }
    }
}
