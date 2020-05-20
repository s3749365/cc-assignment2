package edu.rmit.cc.a2web.core.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;


@Service
public class ApplicationDB {

    private static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static String GOOGLE_DRIVER = "com.google.cloud.sql.mysql.SocketFactory";

    @Value("${gcloud.mysql.address}")
    private String mysqlAddress;

    @Value("${gcloud.mysql.db}")
    private String dbName;

    @Value("${gcloud.mysql.username}")
    private String username;

    @Value("${gcloud.mysql.password}")
    private String password;


    public ApplicationDB() {
        init();
    }

    private void init() {

        try {

            Class.forName(JDBC_DRIVER);
            Class.forName(GOOGLE_DRIVER);


        } catch (ClassNotFoundException exception) {

            throw new RuntimeException("DB Driver not found.");

        }
    }

    public Connection getConnection() {

        String connString = String.format("jdbc:mysql:///%s?cloudSqlInstance=%s&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=%s&password=%s", dbName, mysqlAddress, username, password);

        Connection con = null;

        try {
            con = DriverManager.getConnection(connString);
        } catch (Throwable ex) {
            throw new RuntimeException("DB Connection failed");
        }

        return con;

    }
}
