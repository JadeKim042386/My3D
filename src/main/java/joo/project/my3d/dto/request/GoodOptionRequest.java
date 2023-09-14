package joo.project.my3d.dto.request;

import joo.project.my3d.dto.DimensionDto;
import joo.project.my3d.dto.GoodOptionDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class GoodOptionRequest {
        private String optionName;
        private Integer addPrice;
        private String printingTech;
        private String material;
        @NotNull
        private final List<DimensionRequest> dimensions = new ArrayList<>();

        private GoodOptionRequest(String optionName, Integer addPrice, String printingTech, String material) {
                this.optionName = optionName;
                this.addPrice = addPrice;
                this.printingTech = printingTech;
                this.material = material;
        }

        public static GoodOptionRequest of(String optionName, Integer addPrice, String printingTech, String material) {
                return new GoodOptionRequest(optionName, addPrice, printingTech, material);
        }

        public GoodOptionDto toDto(Long articleId) {
                return GoodOptionDto.of(
                        articleId,
                        optionName,
                        addPrice,
                        printingTech,
                        material
                );
        }

        public static GoodOptionRequest from(GoodOptionDto dto) {
                return GoodOptionRequest.of(
                        dto.optionName(),
                        dto.addPrice(),
                        dto.printingTech(),
                        dto.material()
                );
        }

        public List<DimensionDto> toDimensionDtos(Long goodOptionId) {
                return dimensions.stream()
                        .map(dimensionRequest -> dimensionRequest.toDto(goodOptionId))
                        .toList();
        }

}
