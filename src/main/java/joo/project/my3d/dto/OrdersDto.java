package joo.project.my3d.dto;

import joo.project.my3d.domain.Company;
import joo.project.my3d.domain.Orders;
import joo.project.my3d.domain.UserAccount;
import joo.project.my3d.domain.constant.OrderStatus;

import java.time.LocalDateTime;

public record OrdersDto(
        Long id,
        String productName,
        AddressDto addressDto,
        OrderStatus status,
        UserAccountDto userAccountDto,
        CompanyDto companyDto,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static OrdersDto of(Long id, String productName, AddressDto addressDto, OrderStatus status, UserAccountDto userAccountDto, CompanyDto companyDto, LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt, String modifiedBy) {
        return new OrdersDto(id, productName, addressDto, status, userAccountDto, companyDto, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static OrdersDto of(Long id, String productName, AddressDto addressDto, OrderStatus status, UserAccountDto userAccountDto, CompanyDto companyDto) {
        return OrdersDto.of(id, productName, addressDto, status, userAccountDto, companyDto, null, null, null, null);
    }

    /**
     * 주문 상태를 업데이트하기위해 사용
     */
    public static OrdersDto of(Long id, OrderStatus status) {
        return OrdersDto.of(id, null, AddressDto.of(), status, null, null, null, null, null, null);
    }

    public static OrdersDto from(Orders orders) {
        return OrdersDto.of(
                orders.getId(),
                orders.getProductName(),
                AddressDto.from(orders.getAddress()),
                orders.getStatus(),
                UserAccountDto.from(orders.getUserAccount()),
                CompanyDto.from(orders.getCompany()),
                orders.getCreatedAt(),
                orders.getCreatedBy(),
                orders.getModifiedAt(),
                orders.getModifiedBy()
        );
    }

    public Orders toEntity(UserAccount userAccount, Company company) {
        return Orders.of(
                status,
                productName,
                addressDto.toEntity(),
                userAccount,
                company
        );
    }
}
