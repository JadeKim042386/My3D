package joo.project.my3d.dto.request;

import joo.project.my3d.domain.constant.OrderStatus;
import joo.project.my3d.dto.AddressDto;
import joo.project.my3d.dto.OrdersDto;

public record OrdersRequest(
        Long id,
        String productName,
        String zipcode,
        String detail,
        String street,
        OrderStatus status
) {
    public OrdersDto toDto() {
        return OrdersDto.of(
                id,
                productName,
                AddressDto.of(zipcode, street, detail),
                status
        );
    }
}
