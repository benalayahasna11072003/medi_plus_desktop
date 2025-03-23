package utils;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JDBConnection {


    private static final String URL = "jdbc:mysql://localhost:3306/mediplus";

    private static final String USER = "root";
    private static final String PASSWORD = "";

    private static JDBConnection instance;

    private Connection cnx;

    private JDBConnection() {
        try {
            cnx = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected To DATABASE !");
        } catch (SQLException e) {
            System.err.println("Error: "+e.getMessage());
        }
    }

    public static JDBConnection getInstance(){
        if (instance == null) instance = new JDBConnection();
        return instance;
    }

    public Connection getCnx() {
        return cnx;
    }
}