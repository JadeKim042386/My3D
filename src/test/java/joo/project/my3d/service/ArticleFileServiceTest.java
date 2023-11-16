package joo.project.my3d.service;

import joo.project.my3d.domain.ArticleFile;
import joo.project.my3d.domain.DimensionOption;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.repository.ArticleFileRepository;
import joo.project.my3d.service.aws.S3Service;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

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
        DimensionOption dimensionOption = Fixture.getDimensionOption();
        given(articleFileRepository.save(any(ArticleFile.class))).willReturn(articleFile);
        // When
        articleFileService.saveArticleFile(file, dimensionOption);
        // Then
        then(articleFileRepository).should().save(any(ArticleFile.class));
    }

    @DisplayName("모델 파일 수정 - 파일 변경")
    @Test
    void updateArticleFile() throws IOException {
        // Given
        MockMultipartFile file = Fixture.getMultipartFile("NotUpdated");
        // When
        articleFileService.updateArticleFile(file);
        // Then
    }

    @DisplayName("모델 파일 수정 - 파일 변경 없음")
    @Test
    void updateArticleFileNotChange() throws IOException {
        // Given
        MockMultipartFile file = Fixture.getMultipartFile("NotUpdated");
        // When
        articleFileService.updateArticleFile(file);
        // Then
        then(articleFileRepository).shouldHaveNoInteractions();
    }
}
