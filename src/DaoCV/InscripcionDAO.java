package DaoCV;


import ModelCV.Inscripcion;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InscripcionDAO {
    public List<Inscripcion> getAllInscripciones() {
        List<Inscripcion> inscripciones = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            // Complex join to get all necessary display information
            String sql = "SELECT i.id, i.estudiante_id, i.horario_id, i.fecha_inscripcion, " +
                    "s.nombre AS estudiante_nombre, s.apellido AS estudiante_apellido, " +
                    "c.nombre AS curso_nombre, h.dia_semana, h.hora_inicio, h.hora_fin " +
                    "FROM inscripciones i " +
                    "JOIN estudiantes s ON i.estudiante_id = s.id " +
                    "JOIN horarios h ON i.horario_id = h.id " +
                    "JOIN cursos c ON h.curso_id = c.id";
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                String estudianteNombreCompleto = rs.getString("estudiante_nombre") + " " + rs.getString("estudiante_apellido");
                String cursoHorarioInfo = rs.getString("curso_nombre") + " - " + rs.getString("dia_semana") + " " +
                        rs.getTime("hora_inicio") + "-" + rs.getTime("hora_fin");

                Inscripcion inscripcion = new Inscripcion(
                        rs.getInt("id"),
                        rs.getInt("estudiante_id"),
                        rs.getInt("horario_id"),
                        rs.getTimestamp("fecha_inscripcion"), // Use getTimestamp for DATETIME
                        estudianteNombreCompleto,
                        cursoHorarioInfo
                );
                inscripciones.add(inscripcion);
            }
        } catch (SQLException e) {
            System.err.println("Error getting enrollments: " + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* Log or ignore */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Log or ignore */ }
            DBConnection.closeConnection(conn);
        }
        return inscripciones;
    }

    /**
     * Adds a new enrollment to the database.
     * The ID of the new enrollment will be set in the provided Inscripcion object.
     * @param inscripcion The Inscripcion object to add.
     */
    public void addInscripcion(Inscripcion inscripcion) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO inscripciones (estudiante_id, horario_id, fecha_inscripcion) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, inscripcion.getEstudianteId());
            stmt.setInt(2, inscripcion.getHorarioId());
            stmt.setTimestamp(3, new Timestamp(inscripcion.getFechaInscripcion().getTime())); // Convert util.Date to sql.Timestamp
            stmt.executeUpdate();

            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                inscripcion.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            // Check for duplicate entry error (SQLState 23000 for integrity constraint violation)
            if (e.getSQLState().startsWith("23")) {
                System.err.println("Error: Duplicate enrollment. Student is already enrolled in this schedule.");
                // Re-throw a more user-friendly exception or handle it
                try {
                    throw new SQLException("El estudiante ya está inscrito en ese horario.", e);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

            } else {
                System.err.println("Error adding enrollment: " + e.getMessage());
            }
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Log or ignore */ }
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Deletes an enrollment from the database by its ID.
     * @param id The ID of the enrollment to delete.
     */
    public void deleteInscripcion(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM inscripciones WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting enrollment: " + e.getMessage());
        } finally {
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* Log or ignore */ }
            DBConnection.closeConnection(conn);
        }
    }
}

