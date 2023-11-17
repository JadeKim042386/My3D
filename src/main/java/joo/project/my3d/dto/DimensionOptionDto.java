package joo.project.my3d.dto;

import joo.project.my3d.domain.DimensionOption;

public record DimensionOptionDto(
        Long id,
        String optionName
) {
    public static DimensionOptionDto of(Long id, String optionName) {
        return new DimensionOptionDto(id, optionName);
    }

    public static DimensionOptionDto of(String optionName) {
        return DimensionOptionDto.of(null,  optionName);
    }

    public DimensionOption toEntity() {
        return DimensionOption.of(
                optionName
        );
    }

    public static DimensionOptionDto from(DimensionOption dimensionOption) {
        return DimensionOptionDto.of(
                dimensionOption.getId(),
                dimensionOption.getOptionName()
        );
    }
}
