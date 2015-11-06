package com.zoo.accessor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.MappingManager;
import com.datastax.driver.mapping.Result;
import com.google.common.util.concurrent.ListenableFuture;
import com.zoo.Address;
import com.zoo.Phone;

/**
 * 
 * @author yankai913@gmail.com
 * @date 2015-11-4
 *
 */
public class AccessorClient {

    private Cluster cluster;
    private Session session;

    // 放一个公共userId
    private UUID userId = UUID.fromString("fbdf82ed-0063-4796-9c7c-a3d4f47b4b25");


    public Session getSession() {
        return this.session;
    }


    /**
     * 连接集群，创建执行cql的session对象。
     * 
     * @param node
     */
    public void connect(String node) {
        cluster = Cluster.builder().addContactPoint(node).build();
        Metadata metadata = cluster.getMetadata();
        System.out.printf("Connected to cluster: %s\n", metadata.getClusterName());
        for (Host host : metadata.getAllHosts()) {
            System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n", host.getDatacenter(),
                host.getAddress(), host.getRack());
        }
        session = cluster.connect();
        System.out.println();
    }


    public void insert() {
        MappingManager manager = new MappingManager(getSession());
        UserAccessor userAccessor = manager.createAccessor(UserAccessor.class);
        Map<String, Address> addresses = new HashMap<String, Address>();
        Address address = new Address();
        address.setCity("Honolulu");
        address.setStreet("123 Arnold Drive");
        address.setZipCode(95476);
        List<Phone> phones = new ArrayList<Phone>();
        Phone phone1 = new Phone("job1", "10086");
        Phone phone2 = new Phone("job2", "10000");
        phones.add(phone1);
        phones.add(phone2);
        address.setPhones(phones);
        addresses.put("Work", address);
        userAccessor.insertUser(userId, "tom", addresses);
    }


    public void select() {
        // getAll
        MappingManager manager = new MappingManager(getSession());
        UserAccessor userAccessor = manager.createAccessor(UserAccessor.class);
        Result<User> users = userAccessor.getAll();
        for (User user : users) {
            System.out.println("getAll:" + user);
        }
        // getOne
        User user = userAccessor.getOnePosition(userId);
        System.out.println("getOne:" + user);
        // getAllAsync
        ListenableFuture<Result<User>> future = userAccessor.getAllAsync();
        try {
            for (User _user : future.get()) {
                System.out.println("getAsync:" + _user);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void update() {
        MappingManager manager = new MappingManager(getSession());
        UserAccessor userAccessor = manager.createAccessor(UserAccessor.class);
        userAccessor.updateName(userId, "jack");
    }


    public void update2() {
        MappingManager manager = new MappingManager(getSession());
        UserAccessor userAccessor = manager.createAccessor(UserAccessor.class);
        Address address = new Address();
        List<Phone> phones = new ArrayList<Phone>();
        Phone phone2 = new Phone("job2", "10010");
        phones.add(phone2);
        address.setPhones(phones);
        userAccessor.updateAddress(userId, "Work", address);
    }


    public void delete() {
        MappingManager manager = new MappingManager(getSession());
        UserAccessor userAccessor = manager.createAccessor(UserAccessor.class);
        userAccessor.deleteOne(userId);
    }


    public void dropSchema() {
        session.execute("DROP KEYSPACE complex;");
    }


    public void close() {
        session.close();
        cluster.close();
    }


    public static void main(String[] args) {
        AccessorClient client = new AccessorClient();
        try {
            client.connect("127.0.0.1");
            client.select();
            client.insert();
            client.select();
            client.update();
            client.select();
            client.update2();
            client.select();
            client.delete();
            client.select();
            client.dropSchema();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            client.close();
        }
    }
}
