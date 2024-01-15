package joo.project.my3d.dto.request;

import joo.project.my3d.domain.constant.DimUnit;
import joo.project.my3d.dto.DimensionDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class DimensionRequest {
        private String dimName;
        private Float dimValue;
        private DimUnit dimUnit;

        private DimensionRequest(String dimName, Float dimValue, DimUnit dimUnit) {
                this.dimName = dimName;
                this.dimValue = dimValue;
                this.dimUnit = dimUnit;
        }

        public static DimensionRequest of(String dimName, Float dimValue, DimUnit dimUnit) {
                return new DimensionRequest(dimName, dimValue, dimUnit);
        }

        //TODO: dimensionOptionId가 불필요해보임
        public DimensionDto toDto(Long dimensionOptionId) {
                return DimensionDto.of(
                        dimensionOptionId,
                        dimName,
                        dimValue,
                        dimUnit
                );
        }

        public DimensionDto toDto() {
                return toDto(null);
        }

        public static DimensionRequest from(DimensionDto dto) {
                return DimensionRequest.of(
                        dto.dimName(),
                        dto.dimValue(),
                        dto.dimUnit()
                );
        }
}
