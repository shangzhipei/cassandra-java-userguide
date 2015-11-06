package com.zoo.orm;

import java.util.ArrayList;
import java.util.List;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;
import com.google.common.util.concurrent.ListenableFuture;
import com.zoo.Account;
import com.zoo.Address;
import com.zoo.Phone;


public class ORMClient {

    private Cluster cluster;
    private Session session;

    Mapper<Account> mapper;


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
        mapper = new MappingManager(getSession()).mapper(Account.class);
    }


    public void insert() {
        Phone phone = new Phone("home", "707-555-3537");
        List<Phone> phones = new ArrayList<Phone>();
        phones.add(phone);
        Address address = new Address("25800 Arnold Drive", "Sonoma", 95476, phones);
        Account account = new Account("John Doe", "jd@example.com", address);
        mapper.save(account);
    }


    public void select() {
        Account whose = mapper.get("jd@example.com");
        if (whose == null) {
            System.out.println("Account is null");
            return;
        }
        System.out.println("Account name: " + whose.getName());
        // 异步查询
        ListenableFuture<Account> future = mapper.getAsync("jd@example.com");
        try {
            whose = future.get();
            System.out.println("getAsync, Account name: " + whose.getName());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void delete() {
        Account account = new Account("John Doe", "jd@example.com", null);
        mapper.delete(account);
    }


    public void update() {
        // 没有看到mapper对象关于update的api
    }


    public void close() {
        session.close();
        cluster.close();
    }


    public void dropSchema() {
        session.execute("DROP KEYSPACE complex;");
    }


    public static void main(String[] args) {
        ORMClient client = new ORMClient();
        try {
            client.connect("127.0.0.1");
            client.insert();
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
