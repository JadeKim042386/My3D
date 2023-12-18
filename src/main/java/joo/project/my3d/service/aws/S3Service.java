package joo.project.my3d.service.aws;

import joo.project.my3d.exception.ErrorCode;
import joo.project.my3d.exception.FileException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3;

    @Value("${aws.bucketName}")
    private String bucketName;

    /**
     * https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/S3ObjectOperations.java#L213
     */
    public void uploadFile(MultipartFile file, String key) throws IOException {
        // First create a multipart upload and get the upload id
        CreateMultipartUploadRequest createMultipartUploadRequest = CreateMultipartUploadRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        CreateMultipartUploadResponse response = s3.createMultipartUpload(createMultipartUploadRequest);
        String uploadId = response.uploadId();

        // Upload all the different parts of the object
        UploadPartRequest uploadPartRequest = UploadPartRequest.builder()
                .bucket(bucketName)
                .key(key)
                .uploadId(uploadId)
                .partNumber(1).build();

        String etag1 = s3.uploadPart(uploadPartRequest, RequestBody.fromBytes(file.getBytes())).eTag();

        CompletedPart part = CompletedPart.builder().partNumber(1).eTag(etag1).build();

        // Finally call completeMultipartUpload operation to tell S3 to merge all uploaded
        // parts and finish the multipart operation.
        CompletedMultipartUpload completedMultipartUpload = CompletedMultipartUpload.builder()
                .parts(part)
                .build();

        CompleteMultipartUploadRequest completeMultipartUploadRequest =
                CompleteMultipartUploadRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .uploadId(uploadId)
                        .multipartUpload(completedMultipartUpload)
                        .build();

        s3.completeMultipartUpload(completeMultipartUploadRequest);
    }

    /**
     * https://github.com/awsdocs/aws-doc-sdk-examples/blob/main/javav2/example_code/s3/src/main/java/com/example/s3/S3Scenario.java#L322
     * Amazon S3에서 파일 삭제 수행
     * @param key 파일명
     * @throws SdkClientException IO관련 예외
     * @throws S3Exception 파일 삭제 과정에서 알 수 없는 예외
     */
    public void deleteFile(String key) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3.deleteObject(deleteObjectRequest);
    }

    /**
     * https://github.com/awsdocs/aws-doc-sdk-examples/blob/295fc527dbc2e36e55d49286100d9d6ec9502eb3/javav2/example_code/s3/src/main/java/com/example/s3/S3Scenario.java#L241
     * @param key 파일명
     */
    public byte[] downloadFile(String key) {
        try {
            GetObjectRequest objectRequest = GetObjectRequest
                    .builder()
                    .key(key)
                    .bucket(bucketName)
                    .build();
            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(objectRequest);

            return objectBytes.asByteArray();
        } catch (S3Exception e) {
            log.error(e.awsErrorDetails().errorMessage());
            throw new FileException(ErrorCode.FILE_DOWNLOAD_FAIL, e);
        }
    }

}
