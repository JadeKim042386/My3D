package joo.project.my3d.service.impl;

import joo.project.my3d.exception.FileException;
import joo.project.my3d.exception.constant.ErrorCode;
import joo.project.my3d.service.FileServiceInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class LocalFileService implements FileServiceInterface {

    @Value("${file.save-path}")
    private String path;

    @Override
    public void uploadFile(MultipartFile file, String fileName) throws FileException {
        try (FileOutputStream fos = new FileOutputStream(createFile(fileName))) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            throw new FileException(ErrorCode.FILE_CANT_SAVE);
        }
    }

    @Override
    public void deleteFile(String fileName) {
        File file = createFile(fileName);
        if (file.exists()) {
            if (!file.delete()) {
                throw new FileException(ErrorCode.FAILED_DELETE);
            }
        } else {
            throw new FileException(ErrorCode.FILE_NOT_FOUND);
        }
    }

    @Override
    public byte[] downloadFile(String fileName) {
        try (FileInputStream fis = new FileInputStream(createFile(fileName))) {
            return fis.readAllBytes();
        } catch (IOException e) {
            throw new FileException(ErrorCode.FILE_DOWNLOAD_FAIL);
        }
    }

    private File createFile(String fileName) {
        return new File(path + fileName);
    }
}
