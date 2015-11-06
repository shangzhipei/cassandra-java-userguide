package com.zoo;

import java.util.List;

import com.datastax.driver.mapping.annotations.Field;
import com.datastax.driver.mapping.annotations.Frozen;
import com.datastax.driver.mapping.annotations.UDT;


@UDT(keyspace = "complex", name = "address")
public class Address extends BaseEntity {

    private String street;

    private String city;

    @Field(name = "zip_code")
    private int zipCode;

    @Frozen("list<frozen<phone>>")
    private List<Phone> phones;


    public Address() {

    }


    public Address(String street, String city, int zipCode, List<Phone> phones) {
        this.street = street;
        this.city = city;
        this.zipCode = zipCode;
        this.phones = phones;
    }


    public String getStreet() {
        return street;
    }


    public void setStreet(String street) {
        this.street = street;
    }


    public String getCity() {
        return city;
    }


    public void setCity(String city) {
        this.city = city;
    }


    public int getZipCode() {
        return zipCode;
    }


    public void setZipCode(int zipCode) {
        this.zipCode = zipCode;
    }


    public List<Phone> getPhones() {
        return phones;
    }


    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

}
