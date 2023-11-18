package joo.project.my3d.service;

import joo.project.my3d.domain.Dimension;
import joo.project.my3d.domain.DimensionOption;
import joo.project.my3d.dto.DimensionDto;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.repository.DimensionOptionRepository;
import joo.project.my3d.repository.DimensionRepository;
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
    @Mock private DimensionOptionRepository dimensionOptionRepository;

    @DisplayName("모델 게시글 내의 치수 목록 조회")
    @Test
    void getDimensions() {
        // Given
        Long dimensionOptionId = 1L;
        given(dimensionRepository.findAllByDimensionOptionId(dimensionOptionId)).willReturn(List.of());
        // When
        dimensionService.getDimensions(dimensionOptionId);
        // Then
        then(dimensionRepository).should().findAllByDimensionOptionId(dimensionOptionId);
    }

    @DisplayName("치수 저장")
    @Test
    void saveDimension() {
        // Given
        DimensionOption dimensionOption = Fixture.getDimensionOption();
        DimensionDto dimensionDto = FixtureDto.getDimensionDto();
        Dimension dimension = dimensionDto.toEntity(dimensionOption);
        given(dimensionRepository.save(any(Dimension.class))).willReturn(dimension);
        // When
        dimensionService.saveDimension(dimensionDto);
        // Then
        then(dimensionRepository).should().save(any(Dimension.class));
    }

    @DisplayName("치수 수정")
    @Test
    void updateDimension() {
        // Given
        Long dimensionId = 1L;
        DimensionOption dimensionOption = dimensionOptionRepository.getReferenceById(1L);
        DimensionDto dimensionDto = FixtureDto.getDimensionDto();
        Dimension dimension = dimensionDto.toEntity(dimensionOption);
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
