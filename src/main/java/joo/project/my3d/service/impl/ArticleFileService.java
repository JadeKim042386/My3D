package joo.project.my3d.service.impl;

import joo.project.my3d.domain.ArticleFile;
import joo.project.my3d.domain.DimensionOption;
import joo.project.my3d.dto.request.ArticleFormRequest;
import joo.project.my3d.exception.FileException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.repository.ArticleFileRepository;
import joo.project.my3d.repository.ArticleRepository;
import joo.project.my3d.service.ArticleFileServiceInterface;
import joo.project.my3d.service.FileServiceInterface;
import joo.project.my3d.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleFileService implements ArticleFileServiceInterface {
    private final ArticleRepository articleRepository;
    private final FileServiceInterface fileService;

    /**
     * @throws FileException 파일이 정상적이지 않을 경우 발생하는 예외
     */
    @Transactional
    @Override
    public void updateArticleFile(ArticleFormRequest articleFormRequest, ArticleFile articleFile) {
        updateFile(articleFile, articleFormRequest.getModelFile());

        // 치수 옵션 업데이트
        DimensionOption dimensionOption = articleFile.getDimensionOption();
        dimensionOption.setOptionName(
                articleFormRequest.getDimensionOptions().get(0).getOptionName());

        // 치수 업데이트
        dimensionOption.updateDimensions(
                articleFormRequest.getDimensionOptions().get(0).toDimensionEntities(dimensionOption));
    }

    /**
     * @throws FileException S3 파일 삭제 또는 DB에 존재하지 않을 경우 발생하는 예외
     */
    @Override
    public void deleteFile(Long articleId) {
        try {
            fileService.deleteFile(searchFileName(articleId));
        } catch (SdkClientException | S3Exception e) {
            log.error("S3 파일 삭제 실패 - articleId: {}", articleId);
            throw new FileException(ErrorCode.FAILED_DELETE, e);
        }
    }

    @Override
    public byte[] download(String fileName) {
        return fileService.downloadFile(fileName);
    }

    @Override
    public void updateFile(ArticleFile articleFile, MultipartFile file) {
        String fileName = "";
        try {
            // 업데이트 여부 확인
            if (!new String(file.getBytes()).equals("NotUpdated") && file.getSize() > 0) {
                // S3에 저장한 파일 삭제
                fileName = articleFile.getFileName();
                fileService.deleteFile(fileName);

                String originalFileName = file.getOriginalFilename();
                String extension = FileUtils.getExtension(originalFileName);
                fileName = UUID.randomUUID() + "." + extension;
                // 업데이트된 파일로 S3에 저장
                fileService.uploadFile(file, fileName);

                // 파일 업데이트
                long byteSize = file.getSize();
                articleFile.update(byteSize, originalFileName, fileName, extension);
            }
        } catch (IOException e) {
            log.error("파일을 읽을 수 없습니다. - Filename: {}", fileName);
            throw new FileException(ErrorCode.FILE_CANT_SAVE, e);
        }
    }

    @Override
    public String searchFileName(Long articleId) {
        return articleRepository.findFileNameById(articleId)
                .orElseThrow(() -> new FileException(ErrorCode.FILE_NOT_FOUND));
    }
}
