package joo.project.my3d.dto.request;

import joo.project.my3d.domain.Dimension;
import joo.project.my3d.domain.DimensionOption;
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

    public static DimensionRequest from(DimensionDto dto) {
        return DimensionRequest.of(dto.dimName(), dto.dimValue(), dto.dimUnit());
    }

    public DimensionDto toDto() {
        return DimensionDto.of(dimName, dimValue, dimUnit);
    }

    public Dimension toEntity(DimensionOption dimensionOption) {
        return Dimension.of(dimensionOption, dimName, dimValue, dimUnit);
    }
}
