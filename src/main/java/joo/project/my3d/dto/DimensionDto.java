package joo.project.my3d.dto;

import joo.project.my3d.domain.Dimension;
import joo.project.my3d.domain.DimensionOption;
import joo.project.my3d.domain.constant.DimUnit;

public record DimensionDto(
        Long id,
        Long dimensionOptionId,
        String dimName,
        Float dimValue,
        DimUnit dimUnit
) {

    public static DimensionDto of(Long id, Long dimensionOptionId, String dimName, Float dimValue, DimUnit dimUnit) {
        return new DimensionDto(id, dimensionOptionId, dimName, dimValue, dimUnit);
    }

    public static DimensionDto of(Long dimensionOptionId, String dimName, Float dimValue, DimUnit dimUnit) {
        return DimensionDto.of(null, dimensionOptionId, dimName, dimValue, dimUnit);
    }

    public Dimension toEntity(DimensionOption dimensionOption) {
        return Dimension.of(
                dimensionOption,
                dimName,
                dimValue,
                dimUnit
        );
    }

    public static DimensionDto from(Dimension dimension) {
        return DimensionDto.of(
                dimension.getId(),
                dimension.getDimensionOption().getId(),
                dimension.getDimName(),
                dimension.getDimValue(),
                dimension.getDimUnit()
        );
    }
}
