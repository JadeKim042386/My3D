package joo.project.my3d.service;

import joo.project.my3d.dto.ArticleFileDto;
import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.exception.FileException;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.fixture.FixtureDto;
import joo.project.my3d.repository.ArticleFileRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@DisplayName("비지니스 로직 - 모델 파일")
@ExtendWith(MockitoExtension.class)
class ArticleFileServiceTest {

    @InjectMocks private ArticleFileService articleFileService;
    @Mock private ArticleFileRepository articleFileRepository;

    @BeforeEach
    void beforeEach() throws IllegalAccessException {
        FieldUtils.writeField(articleFileService, "modelPath", "./", true);
    }

    @DisplayName("모델 파일 저장")
    @Test
    void saveArticleFile() throws IOException {
        // Given
        MockMultipartFile file = Fixture.getMultipartFile();
        // When
        articleFileService.saveArticleFile(file);
        // Then
    }

    @DisplayName("모델 파일 수정 - 파일 변경")
    @Test
    void updateArticleFile() throws IOException {
        // Given
        MockMultipartFile file = Fixture.getMultipartFile();
        ArticleFileDto articleFile = FixtureDto.getArticleFileDto();
        Long articleFileId = articleFile.id();
        willDoNothing().given(articleFileRepository).deleteById(articleFileId);
        // When
        articleFileService.updateArticleFile(file, articleFile);
        // Then
        then(articleFileRepository).should().deleteById(articleFileId);
    }

    @DisplayName("모델 파일 수정 - 파일 변경 없음")
    @Test
    void updateArticleFileNotChange() throws IOException {
        // Given
        MockMultipartFile file = Fixture.getMultipartFile("NotUpdated");
        ArticleFileDto articleFile = FixtureDto.getArticleFileDto();
        // When
        articleFileService.updateArticleFile(file, articleFile);
        // Then
        then(articleFileRepository).shouldHaveNoInteractions();
    }
}
