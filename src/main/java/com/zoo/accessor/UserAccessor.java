package com.zoo.accessor;

import java.util.Map;
import java.util.UUID;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.Result;
import com.datastax.driver.mapping.annotations.Accessor;
import com.datastax.driver.mapping.annotations.Param;
import com.datastax.driver.mapping.annotations.Query;
import com.google.common.util.concurrent.ListenableFuture;
import com.zoo.Address;


@Accessor
public interface UserAccessor {

    @Query("SELECT * FROM complex.users WHERE id = ?")
    User getOnePosition(UUID userId);


    @Query("SELECT * FROM complex.users")
    Result<User> getAll();


    @Query("SELECT * FROM complex.users")
    ListenableFuture<Result<User>> getAllAsync();


    @Query("insert into complex.users(id, name, addresses) values (:id, :name, :addresses)")
    ResultSet insertUser(@Param("id") UUID id, @Param("name") String name,
            @Param("addresses") Map<String, Address> addresses);


    @Query("UPDATE complex.users SET name= :name WHERE id= :id")
    ResultSet updateName(@Param("id") UUID id, @Param("name") String name);


    @Query("UPDATE complex.users SET addresses[:name]=:address WHERE id= :id")
    ResultSet updateAddress(@Param("id") UUID id, @Param("name") String addressName,
            @Param("address") Address address);


    @Query("DELETE FROM complex.users WHERE id = ?")
    ResultSet deleteOne(UUID userId);
}
