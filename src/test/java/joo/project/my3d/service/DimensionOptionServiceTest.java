package joo.project.my3d.service;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.DimensionOption;
import joo.project.my3d.dto.DimensionOptionDto;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.repository.ArticleRepository;
import joo.project.my3d.repository.DimensionOptionRepository;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;


@ActiveProfiles("test")
@DisplayName("비지니스 로직 - 치수 옵션")
@ExtendWith(MockitoExtension.class)
class DimensionOptionServiceTest {
    @InjectMocks private DimensionOptionService dimensionOptionService;
    @Mock private DimensionOptionRepository dimensionOptionRepository;

    @DisplayName("치수 옵션 저장")
    @Test
    void saveDimensionOption() {
        // Given
        DimensionOptionDto dimensionOptionDto = FixtureDto.getDimensionOptionDto();
        DimensionOption dimensionOption = dimensionOptionDto.toEntity();
        given(dimensionOptionRepository.save(any(DimensionOption.class))).willReturn(dimensionOption);
        // When
        dimensionOptionService.saveDimensionOption(dimensionOptionDto);
        // Then
        then(dimensionOptionRepository).should().save(any(DimensionOption.class));
    }

    @DisplayName("치수 옵션 수정")
    @Test
    void updateDimensionOption() {
        // Given
        Long dimensionOptionId = 1L;
        DimensionOptionDto dimensionOptionDto = FixtureDto.getDimensionOptionDto();
        DimensionOption dimensionOption = dimensionOptionDto.toEntity();
        given(dimensionOptionRepository.getReferenceById(dimensionOptionId)).willReturn(dimensionOption);
        // When
        dimensionOptionService.updateDimensionOption(dimensionOptionDto);
        // Then
        then(dimensionOptionRepository).should().getReferenceById(dimensionOptionId);
    }
}
