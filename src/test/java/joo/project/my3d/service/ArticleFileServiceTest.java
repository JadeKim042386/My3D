package joo.project.my3d.service;

import joo.project.my3d.domain.ArticleFile;
import joo.project.my3d.dto.request.ArticleFormRequest;
import joo.project.my3d.fixture.Fixture;
import joo.project.my3d.repository.ArticleFileRepository;
import joo.project.my3d.service.impl.ArticleFileService;
import joo.project.my3d.service.impl.S3Service;
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

import static org.mockito.BDDMockito.*;

@ActiveProfiles("test")
@DisplayName("비지니스 로직 - 모델 파일")
@ExtendWith(MockitoExtension.class)
class ArticleFileServiceTest {

    @InjectMocks private ArticleFileService articleFileService;
    @Mock private ArticleFileRepository articleFileRepository;
    @Mock private FileServiceInterface fileService;

    @DisplayName("모델 파일 수정")
    @Test
    void updateArticleFile() throws IllegalAccessException {
        // Given
        Long articleId = 1L;
        ArticleFile articleFile = Fixture.getArticleFile();
        ArticleFormRequest articleFormRequest = Fixture.getArticleFormRequest();
        given(articleFileRepository.findByArticleId(anyLong())).willReturn(Optional.of(articleFile));
        willDoNothing().given(fileService).deleteFile(anyString());
        willDoNothing().given(fileService).uploadFile(eq(articleFormRequest.getModelFile()), anyString());
        // When
        articleFileService.updateArticleFile(articleFormRequest, articleId);
        // Then
        then(articleFileRepository).should().findByArticleId(anyLong());
        then(fileService).should().deleteFile(anyString());
        then(fileService).should().uploadFile(eq(articleFormRequest.getModelFile()), anyString());
    }

    @DisplayName("모델 파일 수정 - 파일 변경 없음")
    @Test
    void updateArticleFile_NotChange() throws IllegalAccessException {
        // Given
        Long articleId = 1L;
        MockMultipartFile file = Fixture.getMultipartFile("NotUpdated");
        ArticleFormRequest articleFormRequest = Fixture.getArticleFormRequest(file);
        ArticleFile articleFile = Fixture.getArticleFile();
        given(articleFileRepository.findByArticleId(anyLong())).willReturn(Optional.of(articleFile));
        // When
        articleFileService.updateArticleFile(articleFormRequest, articleId);
        // Then
        then(articleFileRepository).should().findByArticleId(anyLong());
        then(fileService).shouldHaveNoInteractions();
    }
}
