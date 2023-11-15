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
    @Mock private ArticleRepository articleRepository;

    @DisplayName("모델 게시글 내의 치수 옵션 목록 조회")
    @Test
    void getDimensionOptions() {
        // Given
        Long articleId = 1L;
        given(dimensionOptionRepository.findByArticleId(articleId)).willReturn(List.of());
        // When
        dimensionOptionService.getDimensionOptions(articleId);
        // Then
        then(dimensionOptionRepository).should().findByArticleId(articleId);
    }

    @DisplayName("치수 옵션 저장")
    @Test
    void saveDimensionOption() {
        // Given
        DimensionOptionDto dimensionOptionDto = FixtureDto.getDimensionOptionDto();
        Article article = Fixture.getArticle();
        DimensionOption dimensionOption = dimensionOptionDto.toEntity(article);
        given(articleRepository.getReferenceById(dimensionOptionDto.articleId())).willReturn(article);
        given(dimensionOptionRepository.save(any(DimensionOption.class))).willReturn(dimensionOption);
        // When
        dimensionOptionService.saveDimensionOption(dimensionOptionDto);
        // Then
        then(articleRepository).should().getReferenceById(dimensionOptionDto.articleId());
        then(dimensionOptionRepository).should().save(any(DimensionOption.class));
    }

    @DisplayName("치수 옵션 수정")
    @Test
    void updateDimensionOption() {
        // Given
        Long dimensionOptionId = 1L;
        DimensionOptionDto dimensionOptionDto = FixtureDto.getDimensionOptionDto();
        Article article = Fixture.getArticle();
        DimensionOption dimensionOption = dimensionOptionDto.toEntity(article);
        given(dimensionOptionRepository.getReferenceById(dimensionOptionId)).willReturn(dimensionOption);
        // When
        dimensionOptionService.updateDimensionOption(dimensionOptionDto);
        // Then
        then(dimensionOptionRepository).should().getReferenceById(dimensionOptionId);
    }

    @DisplayName("치수 옵션 삭제")
    @Test
    void deleteDimensionOption() {
        // Given
        Long dimensionOptionId = 1L;
        willDoNothing().given(dimensionOptionRepository).deleteById(dimensionOptionId);
        // When
        dimensionOptionService.deleteDimensionOption(dimensionOptionId);
        // Then
        then(dimensionOptionRepository).should().deleteById(dimensionOptionId);
    }
}
