package joo.project.my3d.service;

import joo.project.my3d.domain.ArticleFile;
import joo.project.my3d.dto.request.ArticleFormRequest;
import org.springframework.web.multipart.MultipartFile;

public interface ArticleFileServiceInterface {
    /**
     * DB의 ArticleFile 수정
     */
    void updateArticleFile(ArticleFormRequest articleFormRequest, Long articleId);
    /**
     * File 삭제
     */
    void deleteFile(Long articleId);
    /**
     * File 다운로드
     */
    byte[] download(Long articleId);
    /**
     * 기존 File 삭제 후 업데이트된 File로 저장
     */
    void updateFile(ArticleFile articleFile, MultipartFile file);
    /**
     * 파일명 조회
     */
    String searchFileName(Long articleId);
}
