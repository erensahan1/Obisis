package universitymanagmentsystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

  
    private static final String URL = "jdbc:sqlite:C:/Users/Casper/Desktop/universityManagmentSystem.db";

  
    private static DatabaseConnection instance;

    private Connection connection;
    

  
    public DatabaseConnection() {
       
        
        try {
            connection = DriverManager.getConnection(URL);
            System.out.println("Connection Successful!");
        } catch (SQLException e) {
            System.out.println("Connection Failed: " + e.getMessage());
        }
    }

    
    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DatabaseConnection();
        } else if (instance.connection.isClosed()) {
           
            instance = new DatabaseConnection();
        }
        return instance;
    }

 
    public Connection getConnection() {
        return connection;
    }

   
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection Closed!");
            }
        } catch (SQLException e) {
            System.out.println("Error Closing Connection: " + e.getMessage());
        }
    }
}
