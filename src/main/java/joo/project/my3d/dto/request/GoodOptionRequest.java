package joo.project.my3d.dto.request;

public record GoodOptionRequest(
        String optionName,
        Integer addPrice,
        String printingTech,
        String material
) {
}
