package parkinggarage.db;

import parkinggarage.model.Reservation;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by melle on 3-2-2017.
 */
public class DatabaseConnection {

    // The connection to the database
    public Connection connection;

    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://13.94.252.194/c10_parkinggarage";

    //  Database credentials
    static final String USER = "c10_silvan";
    static final String PASS = "parkinggarage";

    /**
     * Trying to connect tot the database
     */
    public DatabaseConnection() {
        try {
            // Load the JDBC DRIVER
            Class.forName(JDBC_DRIVER);
            // Open a connection
            System.out.println("-- Connecting to database --");
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
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

    /**
     * Getting data of the database
     * @return database data
     */
    public ArrayList<Reservation> getReservations() {
        ArrayList<Reservation> reservations = new ArrayList<>();
        try {
            // Execute query
            Statement stmt = connection.createStatement();
            String sql = "SELECT * FROM reservation;";
            System.out.println("Executing: "+sql);
            ResultSet rs = stmt.executeQuery(sql);

            while(rs.next()) {
                reservations.add(new Reservation(
                        rs.getString("name"),
                        rs.getInt("day"),
                        rs.getInt("hours"),
                        rs.getInt("minutes"),
                        rs.getInt("duration")
                        ));
                System.out.println(String.format("\t name:%s day:%d hour:%d min:%d dur:%d",
                        rs.getString("name"),
                        rs.getInt("day"),
                        rs.getInt("hours"),
                        rs.getInt("minutes"),
                        rs.getInt("duration")));
            }
            // Closing resources
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return reservations;
    }
}
