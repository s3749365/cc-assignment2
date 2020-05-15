package edu.rmit.cc.a2web.core.config;

import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;


@Service
public class ApplicationDB {

    private static String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static String GOOGLE_DRIVER="com.google.cloud.sql.mysql.SocketFactory";


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

        Connection con = null;


        try {

            con = DriverManager.getConnection("jdbc:mysql:///parking?cloudSqlInstance=cca2-277004:australia-southeast1:parkinginfo&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=root&password=root");
        } catch (Throwable ex) {
            throw new RuntimeException("DB Connection failed");
        }

        return con;

    }
}
