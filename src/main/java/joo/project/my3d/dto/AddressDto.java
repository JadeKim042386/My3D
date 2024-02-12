package joo.project.my3d.dto;

import joo.project.my3d.domain.Address;

public record AddressDto(String zipcode, String street, String detail) {
    public static AddressDto of() {
        return AddressDto.of(null, null, null);
    }

    public static AddressDto of(String zipcode, String address, String detailAddress) {
        return new AddressDto(zipcode, address, detailAddress);
    }

    public static AddressDto from(Address address) {
        return new AddressDto(address.getZipcode(), address.getStreet(), address.getDetail());
    }

    public Address toEntity() {
        return Address.of(zipcode, street, detail);
    }
}
