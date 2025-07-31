package DaoCV;

import ModelCV.Estudiante;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDAO {
    public List<Estudiante> getAllEstudiantes() {
        List<Estudiante> estudiantes = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT * FROM estudiantes";
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Estudiante estudiante = new Estudiante(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getDate("fecha_nacimiento"),
                        rs.getString("direccion"),
                        rs.getString("telefono"),
                        rs.getString("email")
                );
                estudiantes.add(estudiante);
            }
        } catch (SQLException e) {
            System.err.println("Error getting students: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Log or ignore */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Log or ignore */ }
            DBConnection.closeConnection(conn);
        }
        return estudiantes;
    }

    /**
     * Adds a new student to the database.
     * The ID of the new student will be set in the provided Estudiante object.
     * @param estudiante The Estudiante object to add.
     */
    public void addEstudiante(Estudiante estudiante) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO estudiantes (nombre, apellido, fecha_nacimiento, direccion, telefono, email) VALUES (?, ?, ?, ?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, estudiante.getNombre());
            stmt.setString(2, estudiante.getApellido());
            stmt.setDate(3, new java.sql.Date(estudiante.getFechaNacimiento().getTime())); // Convert util.Date to sql.Date
            stmt.setString(4, estudiante.getDireccion());
            stmt.setString(5, estudiante.getTelefono());
            stmt.setString(6, estudiante.getEmail());
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                estudiante.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            System.err.println("Error adding student: " + e.getMessage());
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Log or ignore */ }
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Updates an existing student in the database.
     * @param estudiante The Estudiante object with updated information.
     */
    public void updateEstudiante(Estudiante estudiante) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE estudiantes SET nombre = ?, apellido = ?, fecha_nacimiento = ?, direccion = ?, telefono = ?, email = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, estudiante.getNombre());
            stmt.setString(2, estudiante.getApellido());
            stmt.setDate(3, new java.sql.Date(estudiante.getFechaNacimiento().getTime()));
            stmt.setString(4, estudiante.getDireccion());
            stmt.setString(5, estudiante.getTelefono());
            stmt.setString(6, estudiante.getEmail());
            stmt.setInt(7, estudiante.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Log or ignore */ }
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Deletes a student from the database by their ID.
     * @param id The ID of the student to delete.
     */
    public void deleteEstudiante(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM estudiantes WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Log or ignore */ }
            DBConnection.closeConnection(conn);
        }
    }
}
