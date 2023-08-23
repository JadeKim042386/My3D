package joo.project.my3d.domain;

import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class Company {
    private String companyName;
    private String homepage;

    protected Company() {
    }

    private Company(String companyName, String homepage) {
        this.companyName = companyName;
        this.homepage = homepage;
    }

    public static Company of(String companyName, String homepage) {
        return new Company(companyName, homepage);
    }
}
