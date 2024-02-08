package joo.project.my3d.config;

import joo.project.my3d.service.FileServiceInterface;
import joo.project.my3d.service.impl.LocalFileService;
import joo.project.my3d.service.impl.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Arrays;

@Configuration
public class AppConfig {

    @Value("${file.path}")
    private String localPath;
    @Value("${aws.s3.url}")
    private String s3Path;
    public static String filePath;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.AP_NORTHEAST_2)
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }

    @Bean
    public FileServiceInterface fileService(Environment env, S3Service s3Service, LocalFileService localFileService) {
        String activatedProfile = Arrays.stream(env.getActiveProfiles()).findFirst().orElse("local");
        if (activatedProfile.startsWith("prod")) {
            filePath = s3Path;
            return s3Service;
        } else {
            filePath = localPath;
            return localFileService;
        }
    }
}
