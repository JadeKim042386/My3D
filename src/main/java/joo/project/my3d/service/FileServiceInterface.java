package joo.project.my3d.service;

import joo.project.my3d.exception.FileException;
import org.springframework.web.multipart.MultipartFile;

public interface FileServiceInterface {
    void uploadFile(MultipartFile file, String fileName) throws FileException;
    void deleteFile(String fileName) throws FileException;
    byte[] downloadFile(String fileName) throws FileException;
}
