package joo.project.my3d.service;

import joo.project.my3d.domain.ArticleFile;
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
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleFileService {

    private final ArticleFileRepository articleFileRepository;

    @Value("${model.path}")
    private String modelPath;

    public void saveArticleFile(MultipartFile file) {
        Optional<ArticleFile> savedFile = articleFileRepository.findByFileName(file.getOriginalFilename());
        String fileName;

        //파일 저장(UUID를 파일명으로 저장)
        if (Objects.isNull(savedFile)){
            String extension = FileUtils.getExtension(file.getOriginalFilename());
            fileName = UUID.randomUUID() + "." + extension;
        } else {
            fileName = savedFile.get().getFileName();
        }

        try {
            file.transferTo(new File(modelPath + fileName));
        } catch (IOException e) {
            log.error("File path: {}, MultipartFile: {}", modelPath + fileName, file);
            throw new FileException(ErrorCode.FILE_CANT_SAVE);
        }

    }
}
