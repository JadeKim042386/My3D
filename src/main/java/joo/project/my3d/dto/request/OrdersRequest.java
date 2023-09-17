package joo.project.my3d.dto.request;

import joo.project.my3d.domain.constant.OrderStatus;
import joo.project.my3d.dto.OrdersDto;

public record OrdersRequest(
        Long id,
        OrderStatus status
) {
    public OrdersDto toDto() {
        return OrdersDto.of(
                id,
                status
        );
    }
}
