package joo.project.my3d.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Address {
    private String zipcode; //우편번호
    private String street; //주소
    private String detail; //상세주소

    protected Address() {
    }

    private Address(String zipcode, String street, String detail) {
        this.zipcode = zipcode;
        this.street = street;
        this.detail = detail;
    }

    public static Address of(String zipcode, String street, String detail) {
        return new Address(zipcode, street, detail);
    }
}
