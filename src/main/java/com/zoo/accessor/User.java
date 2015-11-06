package com.zoo.accessor;

import java.util.Map;
import java.util.UUID;

import com.datastax.driver.mapping.annotations.Column;
import com.datastax.driver.mapping.annotations.Frozen;
import com.datastax.driver.mapping.annotations.Table;
import com.zoo.Address;
import com.zoo.BaseEntity;


@Table(keyspace = "complex", name = "users")
public class User extends BaseEntity {

    private UUID id;

    private String name;

    @Column(name = "addresses")
    @Frozen("map<text,frozen<address>>")
    private Map<String, Address> address;


    public UUID getId() {
        return id;
    }


    public void setId(UUID id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public Map<String, Address> getAddress() {
        return address;
    }


    public void setAddress(Map<String, Address> address) {
        this.address = address;
    }

}
