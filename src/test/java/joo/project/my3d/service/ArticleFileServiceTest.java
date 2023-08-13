package joo.project.my3d.service;

import joo.project.my3d.fixture.Fixture;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

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
        given(articleFileRepository.findByFileName(anyString())).willReturn(any(Optional.class));
        // When
        articleFileService.saveArticleFile(file);
        // Then
        then(articleFileRepository).should().findByFileName(anyString());
    }
}