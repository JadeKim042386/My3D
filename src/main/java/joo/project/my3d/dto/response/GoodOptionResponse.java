package joo.project.my3d.dto.response;

import joo.project.my3d.dto.GoodOptionDto;

public record GoodOptionResponse(
        String optionName,
        Integer addPrice,
        String printingTech,
        String material
) {
    public static GoodOptionResponse of(String optionName, Integer addPrice, String printingTech, String material) {
        return new GoodOptionResponse(optionName, addPrice, printingTech, material);
    }

    public static GoodOptionResponse from(GoodOptionDto dto) {
        return GoodOptionResponse.of(
                dto.optionName(),
                dto.addPrice(),
                dto.printingTech(),
                dto.material()
        );
    }
}
