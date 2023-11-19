package joo.project.my3d.dto.response;

import joo.project.my3d.dto.ArticleFileWithDimensionOptionWithDimensionDto;
import joo.project.my3d.dto.request.DimensionOptionRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public record ArticleFileWithDimensionResponse(
        Long id,
        Long byteSize,
        String originalFilename,
        String fileName,
        String fileExtension,
        List<DimensionOptionResponse> dimensionOptions
) {
    public static ArticleFileWithDimensionResponse of(Long id, Long byteSize, String originalFileName, String fileName, String fileExtension, List<DimensionOptionResponse> dimensionOptions) {
        return new ArticleFileWithDimensionResponse(id, byteSize, originalFileName, fileName, fileExtension, dimensionOptions);
    }

    public static ArticleFileWithDimensionResponse from(ArticleFileWithDimensionOptionWithDimensionDto dto) {
        return ArticleFileWithDimensionResponse.of(
                dto.id(),
                dto.byteSize(),
                dto.originalFileName(),
                dto.fileName(),
                dto.fileExtension(),
                List.of(DimensionOptionResponse.from(dto.dimensionOptionWithDimensionDto()))
        );
    }

    public static ArticleFileWithDimensionResponse from(MultipartFile file, List<DimensionOptionRequest> dimensionOptions) {
        return ArticleFileWithDimensionResponse.of(
                null,
                file.getSize(),
                null,
                null,
                null,
                dimensionOptions.stream().map(DimensionOptionResponse::from).toList()
        );
    }
}
