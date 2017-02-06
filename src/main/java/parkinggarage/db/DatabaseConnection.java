package parkinggarage.db;

import java.sql.*;

/**
 * Created by melle on 3-2-2017.
 */
public class DatabaseConnection {

    // The connection to the database
    Connection connection = null;

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost/inventc_nl_parking";

    //  Database credentials
    static final String USER = "inven_nl_parking";
    static final String PASS = "4hc9HH4MgozU";

    public DatabaseConnection() {
        try {
            // Load the JDBC DRIVER
            Class.forName(JDBC_DRIVER);

            // Open a connection
            System.out.println("Connecting to database");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);

            // Execute query
            System.out.println("Create statement");
            Statement stmt = connection.createStatement();
            String sql = "SELECT * FROM reservation;";
            System.out.println("Executing statement");
            ResultSet rs = stmt.executeQuery(sql);
            System.out.println("Gathering resultset:");

            while(rs.next()) {
                System.out.println("\t"+rs.getString("name"));
            }

            System.out.println("DONE");
            // Close connections
            rs.close();
            stmt.close();
            connection.close();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Could not close connection");
            e.printStackTrace();
        }
    }

}
