package joo.project.my3d.service;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.GoodOption;
import joo.project.my3d.dto.GoodOptionDto;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.repository.ArticleRepository;
import joo.project.my3d.repository.GoodOptionRepository;
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
@DisplayName("비지니스 로직 - 상품옵션")
@ExtendWith(MockitoExtension.class)
class GoodOptionServiceTest {
    @InjectMocks private GoodOptionService goodOptionService;
    @Mock private GoodOptionRepository goodOptionRepository;
    @Mock private ArticleRepository articleRepository;

    @DisplayName("모델 게시글 내의 상품옵션 목록 조회")
    @Test
    void getGoodOptions() {
        // Given
        Long articleId = 1L;
        given(goodOptionRepository.findByArticleId(articleId)).willReturn(List.of());
        // When
        goodOptionService.getGoodOptions(articleId);
        // Then
        then(goodOptionRepository).should().findByArticleId(articleId);
    }

    @DisplayName("상품옵션 저장")
    @Test
    void saveGoodOption() {
        // Given
        GoodOptionDto goodOptionDto = FixtureDto.getGoodOptionDto();
        Article article = Fixture.getArticle();
        GoodOption goodOption = goodOptionDto.toEntity(article);
        given(articleRepository.getReferenceById(goodOptionDto.articleId())).willReturn(article);
        given(goodOptionRepository.save(any(GoodOption.class))).willReturn(goodOption);
        // When
        goodOptionService.saveGoodOption(goodOptionDto);
        // Then
        then(articleRepository).should().getReferenceById(goodOptionDto.articleId());
        then(goodOptionRepository).should().save(any(GoodOption.class));
    }

    @DisplayName("상품옵션 수정")
    @Test
    void updateGoodOption() {
        // Given
        Long goodOptionId = 1L;
        GoodOptionDto goodOptionDto = FixtureDto.getGoodOptionDto(goodOptionId);
        Article article = Fixture.getArticle();
        GoodOption goodOption = goodOptionDto.toEntity(article);
        given(goodOptionRepository.getReferenceById(goodOptionId)).willReturn(goodOption);
        // When
        goodOptionService.updateGoodOption(goodOptionDto);
        // Then
        then(goodOptionRepository).should().getReferenceById(goodOptionId);
    }

    @DisplayName("상품옵션 삭제")
    @Test
    void deleteGoodOption() {
        // Given
        Long goodOptionId = 1L;
        willDoNothing().given(goodOptionRepository).deleteById(goodOptionId);
        // When
        goodOptionService.deleteGoodOption(goodOptionId);
        // Then
        then(goodOptionRepository).should().deleteById(goodOptionId);
    }
}