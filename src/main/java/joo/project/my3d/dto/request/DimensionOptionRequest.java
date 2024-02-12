package joo.project.my3d.dto.request;

import joo.project.my3d.domain.Dimension;
import joo.project.my3d.domain.DimensionOption;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class DimensionOptionRequest {
    @NotNull
    private final List<DimensionRequest> dimensions = new ArrayList<>();

    private String optionName;

    public DimensionOption toDimensionOptionEntity() {
        return DimensionOption.of(optionName);
    }

    public List<Dimension> toDimensionEntities(DimensionOption dimensionOption) {
        return dimensions.stream()
                .map((dimension) -> dimension.toEntity(dimensionOption))
                .toList();
    }
}
