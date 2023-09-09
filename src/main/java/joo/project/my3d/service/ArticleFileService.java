package joo.project.my3d.service;

import joo.project.my3d.domain.Article;
import joo.project.my3d.domain.ArticleFile;
import joo.project.my3d.dto.ArticleFileDto;
import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.exception.FileException;
import joo.project.my3d.repository.ArticleFileRepository;
import joo.project.my3d.utils.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleFileService {

    private final ArticleFileRepository articleFileRepository;

    @Value("${model.abs-path}")
    private String absModelPath;

    /**
     * article ID로 파일 조회
     */
    public List<ArticleFileDto> getArticleFiles(Long articleId) {
        return articleFileRepository.findByArticleId(articleId)
                .stream().map(ArticleFileDto::from)
                .toList();
    }

    @Transactional
    public void saveArticleFile(Article article, MultipartFile file) {
        //파일 저장(UUID를 파일명으로 저장)
        String originalFileName = file.getOriginalFilename();
        String extension = FileUtils.getExtension(originalFileName);
        String fileName = UUID.randomUUID() + "." + extension;

        try {
            file.transferTo(new File(absModelPath + fileName));
            long byteSize = file.getSize();
            articleFileRepository.save(
                ArticleFile.of(
                    article,
                    byteSize,
                    originalFileName,
                    fileName,
                    extension
                )
            );
        } catch (IOException e) {
            log.error("File path: {}, MultipartFile: {}", absModelPath + fileName, file);
            throw new FileException(ErrorCode.FILE_CANT_SAVE);
        }
    }

    public boolean updateArticleFile(Article article, List<MultipartFile> files, List<ArticleFileDto> articleFiles) {
        try {
            //업데이트 여부 확인
            boolean isUpdated = false;
            for (int i=0; i < files.size(); i++) {
                if (!new String(files.get(i).getBytes()).equals("NotUpdated")) {
                    isUpdated = true;
                    break;
                }
            }
            return isUpdated;
        } catch (IOException e) {
            log.error("잘못된 파일: {}", files);
            throw new FileException(ErrorCode.FILE_NOT_VALID);
        }
    }

    @Transactional
    public void deleteArticleFile(Long articleFileId) {
        ArticleFile articleFile = articleFileRepository.getReferenceById(articleFileId);
        String fileName = articleFile.getFileName();

        //파일 삭제
        try {
            Files.delete(Paths.get(absModelPath + fileName));
            //데이터 삭제
            articleFileRepository.deleteById(articleFileId);
        } catch (IOException e) {
            log.error("File path: {}", absModelPath + fileName);
            throw new FileException(ErrorCode.FILE_CANT_SAVE);
        }
    }
}
