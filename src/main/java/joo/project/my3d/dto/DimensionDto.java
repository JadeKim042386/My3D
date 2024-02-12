package joo.project.my3d.dto;

import joo.project.my3d.domain.Dimension;
import joo.project.my3d.domain.DimensionOption;
import joo.project.my3d.domain.constant.DimUnit;

public record DimensionDto(Long id, String dimName, Float dimValue, DimUnit dimUnit) {

    public static DimensionDto of(Long id, String dimName, Float dimValue, DimUnit dimUnit) {
        return new DimensionDto(id, dimName, dimValue, dimUnit);
    }

    public static DimensionDto of(String dimName, Float dimValue, DimUnit dimUnit) {
        return DimensionDto.of(null, dimName, dimValue, dimUnit);
    }

    public static DimensionDto from(Dimension dimension) {
        return DimensionDto.of(
                dimension.getId(), dimension.getDimName(), dimension.getDimValue(), dimension.getDimUnit());
    }

    public Dimension toEntity(DimensionOption dimensionOption) {
        return Dimension.of(dimensionOption, dimName, dimValue, dimUnit);
    }
}
