package joo.project.my3d.service;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.Dimension;
import joo.project.my3d.domain.GoodOption;
import joo.project.my3d.dto.DimensionDto;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.repository.DimensionRepository;
import joo.project.my3d.repository.GoodOptionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@DisplayName("비지니스 로직 - 치수")
@ExtendWith(MockitoExtension.class)
class DimensionServiceTest {
    @InjectMocks private DimensionService dimensionService;
    @Mock private DimensionRepository dimensionRepository;
    @Mock private GoodOptionRepository goodOptionRepository;

    @DisplayName("모델 게시글 내의 치수 목록 조회")
    @Test
    void getDimensions() {
        // Given
        Long goodOptionId = 1L;
        given(dimensionRepository.findByGoodOptionId(goodOptionId)).willReturn(List.of());
        // When
        dimensionService.getDimensions(goodOptionId);
        // Then
        then(dimensionRepository).should().findByGoodOptionId(goodOptionId);
    }

    @DisplayName("치수 저장")
    @Test
    void saveDimension() {
        // Given
        DimensionDto dimensionDto = FixtureDto.getDimensionDto();
        Article article = Fixture.getArticle();
        GoodOption goodOption = Fixture.getGoodOption(article);
        Dimension dimension = dimensionDto.toEntity(goodOption);
        given(goodOptionRepository.getReferenceById(dimensionDto.goodOptionId())).willReturn(goodOption);
        given(dimensionRepository.save(any(Dimension.class))).willReturn(dimension);
        // When
        dimensionService.saveDimension(dimensionDto);
        // Then
        then(goodOptionRepository).should().getReferenceById(dimensionDto.goodOptionId());
        then(dimensionRepository).should().save(any(Dimension.class));
    }

    @DisplayName("치수 수정")
    @Test
    void updateDimension() {
        // Given
        Long dimensionId = 1L;
        DimensionDto dimensionDto = FixtureDto.getDimensionDto(dimensionId);
        Article article = Fixture.getArticle();
        GoodOption goodOption = Fixture.getGoodOption(article);
        Dimension dimension = dimensionDto.toEntity(goodOption);
        given(dimensionRepository.getReferenceById(dimensionId)).willReturn(dimension);
        // When
        dimensionService.updateDimension(dimensionDto);
        // Then
        then(dimensionRepository).should().getReferenceById(dimensionId);
    }

    @DisplayName("치수 삭제")
    @Test
    void deleteDimension() {
        // Given
        Long dimensionId = 1L;
        willDoNothing().given(dimensionRepository).deleteById(dimensionId);
        // When
        dimensionService.deleteDimension(dimensionId);
        // Then
        then(dimensionRepository).should().deleteById(dimensionId);
    }
}
