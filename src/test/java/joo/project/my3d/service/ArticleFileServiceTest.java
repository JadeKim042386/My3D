package joo.project.my3d.service;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.ArticleFile;
import joo.project.my3d.dto.ArticleFileDto;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.repository.ArticleFileRepository;
import joo.project.my3d.service.aws.S3Service;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;

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
        Article article = Fixture.getArticle();
        MockMultipartFile file = Fixture.getMultipartFile();
        ArticleFile articleFile = Fixture.getArticleFile(article);
        given(articleFileRepository.save(any(ArticleFile.class))).willReturn(articleFile);
        // When
        articleFileService.saveArticleFile(article, file);
        // Then
        then(articleFileRepository).should().save(any(ArticleFile.class));
    }

    @DisplayName("모델 파일 수정 - 파일 변경")
    @Test
    void updateArticleFile() {
        // Given
        // When
        articleFileService.updateArticleFile(List.of());
        // Then
    }

    @DisplayName("모델 파일 수정 - 파일 변경 없음")
    @Test
    void updateArticleFileNotChange() throws IOException {
        // Given
        MockMultipartFile file = Fixture.getMultipartFile("NotUpdated");
        // When
        articleFileService.updateArticleFile(List.of(file));
        // Then
        then(articleFileRepository).shouldHaveNoInteractions();
    }
}
