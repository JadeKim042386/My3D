package joo.project.my3d.service;

import joo.project.my3d.domain.ArticleFile;
import joo.project.my3d.domain.DimensionOption;
import joo.project.my3d.dto.ArticleFileDto;
import joo.project.my3d.dto.DimensionDto;
import joo.project.my3d.dto.request.ArticleFormRequest;
import joo.project.my3d.exception.FileException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.repository.ArticleFileRepository;
import joo.project.my3d.service.aws.S3Service;
import joo.project.my3d.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleFileService {

    private final ArticleFileRepository articleFileRepository;
    private final S3Service s3Service;

    /**
     * article ID로 파일 조회
     */
    public ArticleFileDto getArticleFile(Long articleId) {

        return ArticleFileDto.from(
                articleFileRepository.findByArticleId(articleId)
                        .orElseThrow(() -> new FileException(ErrorCode.FILE_NOT_FOUND))
        );
    }

    /**
     * 파일의 업데이트 여부를 확인하여 반환
     * @throws FileException 파일이 정상적이지 않을 경우 발생하는 예외
     */
    @Transactional
    public void updateArticleFile(ArticleFormRequest articleFormRequest, Long articleId) {
        try {
            ArticleFile articleFile = articleFileRepository.findByArticleId(articleId)
                    .orElseThrow(() -> new FileException(ErrorCode.FILE_NOT_FOUND));

            updateS3File(articleFile, articleFormRequest.getModelFile());

            //치수 옵션 업데이트
            articleFile.getDimensionOption().setOptionName(
                    articleFormRequest.toDimensionOptionDto().optionName()
            );

            //치수 업데이트
            //TODO: 치수 업데이트 부분은 따로 분리할 수 있을 것 같음
            DimensionOption dimensionOption = articleFile.getDimensionOption();
            List<DimensionDto> dimensions = articleFormRequest.toDimensions();
            dimensionOption.getDimensions().clear();
            dimensionOption.getDimensions().addAll(
                    dimensions.stream()
                            .map(DimensionDto -> DimensionDto.toEntity(dimensionOption))
                            .toList()
            );
        }  catch (FileException e) {
            log.error("게시글 id: {} 에 해당하는 파일을 찾을 수 없습니다.", articleId);
            throw e;
        }
    }

    /**
     * @throws FileException S3 파일 삭제 또는 DB 파일 삭제시 발생하는 예외
     */
    @Transactional
    public void deleteArticleFile(Long articleId) {
        ArticleFile articleFile = articleFileRepository.getReferenceByArticle_Id(articleId);
        String fileName = articleFile.getFileName();
        try {
            //파일 삭제
            s3Service.deleteFile(fileName);
            //데이터 삭제
            articleFileRepository.delete(articleFile);
        } catch (SdkClientException | S3Exception e) {
            log.error("S3 파일 삭제 실패 - Filename: {}", fileName);
            throw new FileException(ErrorCode.FAILED_DELETE, e);
        } catch (IllegalArgumentException e) {
            throw new FileException(ErrorCode.FAILED_DELETE, e);
        }
    }

    private void updateS3File(ArticleFile articleFile, MultipartFile file) {
        String fileName = "";
        try {
            //업데이트 여부 확인
            if (!new String(file.getBytes()).equals("NotUpdated") && file.getSize() > 0) {
                //S3에 저장한 파일 삭제
                fileName = articleFile.getFileName();
                s3Service.deleteFile(fileName);

                String originalFileName = file.getOriginalFilename();
                String extension = FileUtils.getExtension(originalFileName);
                fileName = UUID.randomUUID() + "." + extension;
                //업데이트된 파일로 S3에 저장
                s3Service.uploadFile(file, fileName);

                //파일 업데이트
                long byteSize = file.getSize();
                articleFile.update(byteSize, originalFileName, fileName, extension);
            }
        } catch (SdkClientException | S3Exception e) {
            log.error("S3 파일 삭제 실패 - Filename: {}", fileName);
            throw new FileException(ErrorCode.FAILED_DELETE, e);
        } catch (IOException e) {
            log.error("S3 파일 업로드 실패 - Filename: {}", fileName);
            throw new FileException(ErrorCode.FILE_CANT_SAVE, e);
        }
    }
}
