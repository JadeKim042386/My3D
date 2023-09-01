package joo.project.my3d.dto;

import joo.project.my3d.domain.Price;

public record PriceDto(
        Integer priceValue,
        Integer deliveryPrice
) {
    public static PriceDto of(Integer priceValue, Integer deliveryPrice) {
        return new PriceDto(priceValue, deliveryPrice);
    }

    public static PriceDto from(Price price) {
        return PriceDto.of(
                price.getPriceValue(),
                price.getDeliveryPrice()
        );
    }

    public Price toEntity() {
        return Price.of(
                priceValue,
                deliveryPrice
        );
    }
}
