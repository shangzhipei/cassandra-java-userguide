package com.zoo.simple;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;


/**
 * 
 * @author yankai913@gmail.com
 * @date 2015-9-25
 * 
 */
public class SimpleClient {

    private Cluster cluster;
    private Session session;


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


    /**
     * 创建schema， 创建库：simplex，表：simplex.songs，表：simplex.playlists
     */
    public void createSchema() {
        session.execute("CREATE KEYSPACE IF NOT EXISTS simplex WITH replication "
                + "= {'class':'SimpleStrategy', 'replication_factor':3};");
        session.execute("CREATE TABLE IF NOT EXISTS simplex.songs (" + "id uuid PRIMARY KEY," + "title text,"
                + "album text," + "artist text," + "tags set<text>," + "data blob" + ");");
        session.execute("CREATE TABLE IF NOT EXISTS simplex.playlists (" + "id uuid," + "title text,"
                + "album text, " + "artist text," + "song_id uuid,"
                + "PRIMARY KEY (id, title, album, artist)" + ");");
    }


    /**
     * 插入数据
     */
    public void loadData() {
        session.execute("INSERT INTO simplex.songs (id, title, album, artist, tags) " + "VALUES ("
                + "756716f7-2e54-4715-9f00-91dcbea6cf50," + "'La Petite Tonkinoise',"
                + "'Bye Bye Blackbird'," + "'Joséphine Baker'," + "{'jazz', '2013'})" + ";");
        session.execute("INSERT INTO simplex.playlists (id, song_id, title, album,artist) " + "VALUES ("
                + "2cc9ccb7-6221-4ccb-8387-f22b6a1b354d," + "756716f7-2e54-4715-9f00-91dcbea6cf50,"
                + "'La Petite Tonkinoise'," + "'Bye Bye Blackbird'," + "'Joséphine Baker'" + ");");
    }


    /**
     * 查询simplex.songs
     */
    public void querySchema() {
        ResultSet results2 =
                session.execute("SELECT * FROM simplex.songs "
                        + "WHERE id = 756716f7-2e54-4715-9f00-91dcbea6cf50;");
        for (Row row : results2) {
            System.out.println(row.getUUID("id") + "\t" + row.getString("title") + "\t"
                    + row.getString("album") + "\t" + row.getString("artist") + "\t"
                    + row.getSet("tags", String.class));
        }
        System.out.println();
    }


    /**
     * 修改simplex.songs
     */
    public void updateSchema() {
        session.execute("UPDATE simplex.songs set title = 'La Petite Tonkinoise Updated'"
                + " WHERE id = 756716f7-2e54-4715-9f00-91dcbea6cf50;");
    }


    /**
     * 删除simplex.songs
     */
    public void deleteSchema() {
        session.execute("DELETE FROM simplex.songs " + " WHERE id = 756716f7-2e54-4715-9f00-91dcbea6cf50;");
    }


    /**
     * 删除keyspace，keyspace可以理解成oracle里的schema
     */
    public void dropSchema() {
        session.execute("DROP KEYSPACE simplex;");
    }


    public void close() {
        session.close();
        cluster.close();
    }


    public static void main(String[] args) {
        SimpleClient client = new SimpleClient();
        try {
            client.connect("127.0.0.1");
            client.dropSchema();
            client.createSchema();
            client.loadData();
            System.out.println("before update...");
            client.querySchema();
            System.out.println("after update...");
            client.updateSchema();
            client.querySchema();
            System.out.println();

            client.deleteSchema();
            System.out.println("after delete...");
            client.querySchema();
            System.out.println();
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        finally {
            client.close();
        }
    }
}
