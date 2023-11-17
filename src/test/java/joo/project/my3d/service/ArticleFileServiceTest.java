package joo.project.my3d.service;

import joo.project.my3d.domain.ArticleFile;
import joo.project.my3d.domain.DimensionOption;
import joo.project.my3d.dto.DimensionOptionDto;
import joo.project.my3d.dto.request.ArticleFormRequest;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.repository.ArticleFileRepository;
import joo.project.my3d.service.aws.S3Service;
import joo.project.my3d.utils.FileUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@DisplayName("비지니스 로직 - 모델 파일")
@ExtendWith(MockitoExtension.class)
class ArticleFileServiceTest {

    @InjectMocks private ArticleFileService articleFileService;
    @Mock private ArticleFileRepository articleFileRepository;
    @Mock private S3Service s3Service;

    @DisplayName("모델 파일 저장")
    @Test
    void saveArticleFile() throws IOException {
        // Given
        MockMultipartFile file = Fixture.getMultipartFile();
        ArticleFile articleFile = Fixture.getArticleFile();
        DimensionOptionDto dimensionOptionDto = FixtureDto.getDimensionOptionDto();
        given(articleFileRepository.save(any(ArticleFile.class))).willReturn(articleFile);
        // When
        articleFileService.saveArticleFile(file, dimensionOptionDto);
        // Then
        then(articleFileRepository).should().save(any(ArticleFile.class));
    }

    @DisplayName("모델 파일 저장 - 입력 폼에 추가된 파일과 치수 정보를 저장")
    @Test
    void saveArticleFileWithForm() throws IOException, IllegalAccessException {
        // Given
        ArticleFile articleFile = Fixture.getArticleFile();
        ArticleFormRequest articleFormRequest = Fixture.getArticleFormRequest();
        given(articleFileRepository.save(any(ArticleFile.class))).willReturn(articleFile);
        // When
        articleFileService.saveArticleFileWithForm(articleFormRequest);
        // Then
        then(articleFileRepository).should().save(any(ArticleFile.class));
    }

    @DisplayName("모델 파일 수정")
    @Test
    void updateArticleFile() throws IOException, IllegalAccessException {
        // Given
        Long articleId = 1L;
        ArticleFile articleFile = Fixture.getArticleFile();
        ArticleFormRequest articleFormRequest = Fixture.getArticleFormRequest();
        given(articleFileRepository.findByArticleId(articleId)).willReturn(articleFile);
        willDoNothing().given(s3Service).deleteFile(anyString());
        willDoNothing().given(s3Service).uploadFile(eq(articleFormRequest.getModelFile()), anyString());
        // When
        articleFileService.updateArticleFile(articleFormRequest, articleId);
        // Then
        then(articleFileRepository).should().findByArticleId(articleId);
        then(s3Service).should().deleteFile(anyString());
        then(s3Service).should().uploadFile(eq(articleFormRequest.getModelFile()), anyString());
    }

    @DisplayName("모델 파일 수정 - 파일 변경 없음")
    @Test
    void updateArticleFileNotChange() throws IllegalAccessException {
        // Given
        Long articleId = 1L;
        MockMultipartFile file = Fixture.getMultipartFile("NotUpdated");
        ArticleFormRequest articleFormRequest = Fixture.getArticleFormRequest(file);
        // When
        articleFileService.updateArticleFile(articleFormRequest, articleId);
        // Then
        then(articleFileRepository).shouldHaveNoInteractions();
        then(s3Service).shouldHaveNoInteractions();
    }
}
