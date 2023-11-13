package joo.project.my3d.service;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.ArticleFile;
import joo.project.my3d.dto.ArticleFileDto;
import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.exception.FileException;
import joo.project.my3d.repository.ArticleFileRepository;
import joo.project.my3d.service.aws.S3Service;
import joo.project.my3d.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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

        return ArticleFileDto.from(articleFileRepository.findByArticleId(articleId));
    }

    /**
     * @throws FileException 파일 저장 실패 예외
     */
    @Transactional
    public ArticleFile saveArticleFile(MultipartFile file) {
        //파일 저장(UUID를 파일명으로 저장)
        String originalFileName = file.getOriginalFilename();
        String extension = FileUtils.getExtension(originalFileName);
        String fileName = UUID.randomUUID() + "." + extension;

        try {
            s3Service.uploadFile(file, fileName);
            long byteSize = file.getSize();
            return articleFileRepository.save(
                        ArticleFile.of(
                            byteSize,
                            originalFileName,
                            fileName,
                            extension
                        )
                    );
        } catch (IOException e) {
            log.error("File path: {}, MultipartFile: {}", fileName, file);
            throw new FileException(ErrorCode.FILE_CANT_SAVE);
        }
    }

    /**
     * 파일의 업데이트 여부를 확인하여 반환
     * @throws FileException 파일이 정상적이지 않을 경우 발생하는 예외
     */
    public boolean updateArticleFile(MultipartFile file) {
        try {
            //업데이트 여부 확인
            if (!new String(file.getBytes()).equals("NotUpdated") && file.getSize() > 0) {
                return true;
            }
            return false;
        } catch (IOException e) {
            log.error("잘못된 파일: {}", file);
            throw new FileException(ErrorCode.FILE_NOT_VALID);
        }
    }

    @Transactional
    public void deleteArticleFile(Long articleFileId) {
        ArticleFile articleFile = articleFileRepository.getReferenceById(articleFileId);
        String fileName = articleFile.getFileName();

        //파일 삭제
        s3Service.deleteFile(fileName);
        //데이터 삭제
        articleFileRepository.deleteById(articleFileId);
    }

    /**
     * 게시글에 포함된 파일을 S3과 DB에서 삭제
     */
    @Transactional
    public void deleteArticleFileByArticleId(Long articleId) {

        s3Service.deleteFile(
                articleFileRepository
                        .findByArticleId(articleId)
                        .getFileName()
        );
        articleFileRepository.deleteByArticleId(articleId);
    }
}
