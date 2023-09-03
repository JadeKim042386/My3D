package joo.project.my3d.dto.request;

import joo.project.my3d.domain.constant.DimUnit;

public record DimensionRequest(
        String dimName,
        Float dimValue,
        DimUnit dimUnit
) {
}
