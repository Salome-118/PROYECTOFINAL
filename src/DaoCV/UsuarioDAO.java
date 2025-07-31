package DaoCV;

import ModelCV.Usuario;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {
    public Usuario autenticar(String username, String password) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Usuario usuario = null;

        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?"; // ¡ADVERTENCIA: Contraseña sin hash!
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password); // En una aplicación real, compara el hash
            rs = stmt.executeQuery();

            if (rs.next()) {
                usuario = new Usuario(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("rol")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error al autenticar usuario: " + e.getMessage());
        } finally {
            DBConnection.closeConnection(conn);
            // Cerrar PreparedStatement y ResultSet aquí si no se cierran automáticamente
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignore */ }
            try { if (stmt != null) stmt.close(); } catch (SQLException e) { /* ignore */ }
        }
        return usuario;
    }


}
