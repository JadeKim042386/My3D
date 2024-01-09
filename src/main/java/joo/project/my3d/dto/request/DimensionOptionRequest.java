package joo.project.my3d.dto.request;

import joo.project.my3d.dto.DimensionDto;
import joo.project.my3d.dto.DimensionOptionDto;
import joo.project.my3d.dto.DimensionOptionWithDimensionDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DimensionOptionRequest {
        private String optionName;
        @NotNull
        private final List<DimensionRequest> dimensions = new ArrayList<>();

        private DimensionOptionRequest(String optionName) {
                this.optionName = optionName;
        }

        public static DimensionOptionRequest of(String optionName) {
                return new DimensionOptionRequest(optionName);
        }

        public DimensionOptionDto toDto() {
                return DimensionOptionDto.of(
                        optionName
                );
        }

        public DimensionOptionWithDimensionDto toWithDimensionDto() {
                return DimensionOptionWithDimensionDto.of(
                        optionName,
                        dimensions.stream().map(DimensionRequest::toDto).toList()
                );
        }

        public static DimensionOptionRequest from(DimensionOptionDto dto) {
                return DimensionOptionRequest.of(
                        dto.optionName()
                );
        }

        public List<DimensionDto> toDimensionDtos(Long dimensionOptionId) {
                return dimensions.stream()
                        .map(dimensionRequest -> dimensionRequest.toDto(dimensionOptionId))
                        .toList();
        }

}
