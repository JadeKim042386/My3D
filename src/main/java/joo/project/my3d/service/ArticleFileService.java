package joo.project.my3d.service;

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
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleFileService {

    private final ArticleFileRepository articleFileRepository;

    @Value("${model.path}")
    private String modelPath;

    public ArticleFileDto saveArticleFile(MultipartFile file) {
        //파일 저장(UUID를 파일명으로 저장)
        String extension = FileUtils.getExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + "." + extension;

        try {
            file.transferTo(new File(modelPath + fileName));
            long byteSize = file.getSize();
            String originalFileName = file.getOriginalFilename();
            String fileExtension = FileUtils.getExtension(originalFileName);
//            return ArticleFileDto.of(
//                    byteSize,
//                    originalFileName,
//                    fileName,
//                    fileExtension
//            );
            return null;
        } catch (IOException e) {
            log.error("File path: {}, MultipartFile: {}", modelPath + fileName, file);
            throw new FileException(ErrorCode.FILE_CANT_SAVE);
        }
    }

    /**
     * - 파일이 업데이트되지 않았다면 이전에 저장한 파일명만 반환 <br>
     * - 파일이 업데이트되었다면 이전에 저장한 파일명으로 저장 후 파일명 반환
     */
    @Transactional
    public boolean updateArticleFile(List<MultipartFile> files, List<ArticleFileDto> articleFiles) {
        try {
            boolean isUpdated = false;
            for (int i=0; i < files.size(); i++) {
                if (!new String(files.get(i).getBytes()).equals("NotUpdated")) {
                    ArticleFileDto articleFileDto = articleFiles.get(i);
                    String savedFileName = articleFileDto.fileName();
                    articleFileRepository.deleteById(articleFileDto.id()); //이전에 저장한 파일 정보 삭제
                    files.get(i).transferTo(new File(modelPath + savedFileName));
                    isUpdated = true;
                }
            }
            return isUpdated;
        } catch (IOException e) {
            log.error("잘못된 파일: {}", files);
            throw new FileException(ErrorCode.FILE_NOT_VALID);
        }
    }
}
