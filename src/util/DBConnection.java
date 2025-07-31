package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://b3nfotpoxlph0joy9hjf-mysql.services.clever-cloud.com:3306/b3nfotpoxlph0joy9hjf";
    private static final String USER = "umrztcuiilbkmh3w";
    private static final String PASSWORD = "J6KZIX2gpWpMP2tYHG9s";

    public static Connection getConnection() throws SQLException {
        String sql = "SELECT * FROM Estudiantes";
        try {
            // Cargar el driver JDBC (no siempre necesario en versiones recientes de JDBC)
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            System.err.println("Error: MySQL JDBC Driver no encontrado.");
            throw new SQLException("Driver JDBC no encontrado", e);


        }
    }

    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}
