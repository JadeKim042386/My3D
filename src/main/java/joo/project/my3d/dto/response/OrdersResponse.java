package joo.project.my3d.dto.response;

import joo.project.my3d.domain.Address;
import joo.project.my3d.domain.constant.OrderStatus;
import joo.project.my3d.dto.OrdersDto;

public record OrdersResponse(
        String productName,
        String zipcode,
        String detail,
        String street,
        OrderStatus status
) {
    public static OrdersResponse of(String productName, String zipcode, String detail, String street, OrderStatus status) {
        return new OrdersResponse(productName, zipcode, detail, street, status);
    }

    public static OrdersResponse from(OrdersDto dto) {
        return OrdersResponse.of(
                dto.productName(),
                dto.addressDto().zipcode(),
                dto.addressDto().detailAddress(),
                dto.addressDto().address(),
                dto.status()
        );
    }
}
