package hotelera.bd;

import hotelera.modelos.Habitacion;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HabitacionDAO {
    
    public List<Habitacion> listarTodas() {
        List<Habitacion> lista = new ArrayList<>();
        String sql = "SELECT * FROM habitaciones";
        try (Connection conn = ConexionDB.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Habitacion h = new Habitacion();
                h.setIdHabitacion(rs.getInt("id_habitacion"));
                h.setNumero(rs.getInt("numero"));
                h.setTipo(rs.getString("tipo"));
                h.setPrecio(rs.getDouble("precio"));
                lista.add(h);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    // Devuelve habitaciones que NO están ocupadas en el rango de fechas (reservas activas)
    public List<Habitacion> listarDisponibles(LocalDate ingreso, LocalDate salida) {
        List<Habitacion> disponibles = new ArrayList<>();
        String sql = "SELECT * FROM habitaciones WHERE id_habitacion NOT IN (" +
                     "SELECT id_habitacion FROM reservas WHERE " +
                     "estado = 'activa' AND fecha_ingreso < ? AND fecha_salida > ?" +
                     ")";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(salida));
            ps.setDate(2, Date.valueOf(ingreso));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Habitacion h = new Habitacion();
                h.setIdHabitacion(rs.getInt("id_habitacion"));
                h.setNumero(rs.getInt("numero"));
                h.setTipo(rs.getString("tipo"));
                h.setPrecio(rs.getDouble("precio"));
                disponibles.add(h);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return disponibles;
    }
    public long contarTotalHabitaciones() {
    String sql = "SELECT COUNT(*) FROM habitaciones";
    try (Connection conn = ConexionDB.conectar();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getLong(1);
    } catch (SQLException e) { e.printStackTrace(); }
    return 0;
    }
}