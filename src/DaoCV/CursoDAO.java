package DaoCV;

import ModelCV.Curso;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CursoDAO {
    public List<Curso> getAllCursos() {
        List<Curso> cursos = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            String sql = "SELECT * FROM cursos";
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Curso curso = new Curso(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        rs.getDouble("costo")
                );
                cursos.add(curso);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener cursos: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn);
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
        }
        return cursos;
    }
    public void addCurso(Curso curso) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "INSERT INTO cursos (nombre, descripcion, costo) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setString(1, curso.getNombre());
            stmt.setString(2, curso.getDescripcion());
            stmt.setDouble(3, curso.getCosto());
            stmt.executeUpdate();
            ResultSet generatedKeys = stmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                curso.setId(generatedKeys.getInt(1)); // Asigna el ID generado
            }
        } catch (SQLException e) {
            System.err.println("Error al añadir curso: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn);
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
        }
    }
    public void updateCurso(Curso curso) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE cursos SET nombre = ?, descripcion = ?, costo = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, curso.getNombre());
            stmt.setString(2, curso.getDescripcion());
            stmt.setDouble(3, curso.getCosto());
            stmt.setInt(4, curso.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al actualizar curso: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn);
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

    public void deleteCurso(int id) {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM cursos WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al eliminar curso: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn);
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
        }
    }

}