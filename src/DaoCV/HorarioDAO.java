package DaoCV;

import ModelCV.Horario;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HorarioDAO {

    private String lastError = "";
    public String getLastError() { return lastError; }
    private void setError(SQLException e) {
        lastError = e.getMessage();
        e.printStackTrace();
    }

    public List<Horario> getAllHorarios() {
        List<Horario> lista = new ArrayList<>();
        String sql = "SELECT id, curso_id, dia_semana, hora_inicio, hora_fin FROM horarios";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Horario h = new Horario();
                h.setId(rs.getInt("id"));
                h.setIdCurso(rs.getInt("curso_id"));
                h.setDia(rs.getString("dia_semana"));
                h.setHoraInicio(rs.getString("hora_inicio"));
                h.setHoraFin(rs.getString("hora_fin"));
                lista.add(h);
            }
        } catch (SQLException e) {
            setError(e);
        }
        return lista;
    }

    public boolean insertHorario(Horario h) {
        String sql = "INSERT INTO horarios (curso_id, dia_semana, hora_inicio, hora_fin, cupo_maximo) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, h.getIdCurso());
            ps.setString(2, h.getDia());
            ps.setString(3, h.getHoraInicio());
            ps.setString(4, h.getHoraFin());
            ps.setInt(5, 15); // Puedes poner un valor fijo o agregar cupo_maximo en el formulario
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            setError(e);
            return false;
        }
    }

    public boolean updateHorario(Horario h) {
        String sql = "UPDATE horarios SET curso_id=?, dia_semana=?, hora_inicio=?, hora_fin=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, h.getIdCurso());
            ps.setString(2, h.getDia());
            ps.setString(3, h.getHoraInicio());
            ps.setString(4, h.getHoraFin());
            ps.setInt(5, h.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            setError(e);
            return false;
        }
    }

    public boolean deleteHorario(int id) {
        String sql = "DELETE FROM horarios WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            setError(e);
            return false;
        }
    }
}
