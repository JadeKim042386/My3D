package joo.project.my3d.dto;

import joo.project.my3d.domain.Dimension;
import joo.project.my3d.domain.GoodOption;
import joo.project.my3d.domain.constant.DimUnit;

public record DimensionDto(
        Long id,
        Long goodOptionId,
        String dimName,
        Float dimValue,
        DimUnit dimUnit
) {

    public static DimensionDto of(Long id, Long goodOptionId, String dimName, Float dimValue, DimUnit dimUnit) {
        return new DimensionDto(id, goodOptionId, dimName, dimValue, dimUnit);
    }

    public static DimensionDto of(Long goodOptionId, String dimName, Float dimValue, DimUnit dimUnit) {
        return DimensionDto.of(null, goodOptionId, dimName, dimValue, dimUnit);
    }

    public Dimension toEntity(GoodOption goodOption) {
        return Dimension.of(
                goodOption,
                dimName,
                dimValue,
                dimUnit
        );
    }

    public static DimensionDto from(Dimension dimension) {
        return DimensionDto.of(
                dimension.getId(),
                dimension.getGoodOption().getId(),
                dimension.getDimName(),
                dimension.getDimValue(),
                dimension.getDimUnit()
        );
    }
}
