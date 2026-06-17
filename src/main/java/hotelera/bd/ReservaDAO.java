package hotelera.bd;

import hotelera.modelos.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {
    
    public boolean guardar(Reserva reserva) {
        String sql = "INSERT INTO reservas (id_cliente, id_habitacion, fecha_ingreso, fecha_salida, estado) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reserva.getCliente().getIdCliente());
            ps.setInt(2, reserva.getHabitacion().getIdHabitacion());
            ps.setDate(3, Date.valueOf(reserva.getFechaIngreso()));
            ps.setDate(4, Date.valueOf(reserva.getFechaSalida()));
            ps.setString(5, reserva.getEstado());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    reserva.setIdReserva(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public Reserva obtenerPorId(int id) {
        String sql = "SELECT r.*, c.*, h.* FROM reservas r "
                   + "JOIN clientes c ON r.id_cliente = c.id_cliente "
                   + "JOIN habitaciones h ON r.id_habitacion = h.id_habitacion "
                   + "WHERE r.id_reserva = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapearReserva(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Reserva> listarTodas() {
        List<Reserva> list = new ArrayList<>();
        String sql = "SELECT r.*, c.*, h.* FROM reservas r "
                   + "JOIN clientes c ON r.id_cliente = c.id_cliente "
                   + "JOIN habitaciones h ON r.id_habitacion = h.id_habitacion";
        try (Connection conn = ConexionDB.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapearReserva(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public boolean actualizar(Reserva reserva) {
        String sql = "UPDATE reservas SET id_cliente=?, id_habitacion=?, fecha_ingreso=?, fecha_salida=?, estado=? WHERE id_reserva=?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reserva.getCliente().getIdCliente());
            ps.setInt(2, reserva.getHabitacion().getIdHabitacion());
            ps.setDate(3, Date.valueOf(reserva.getFechaIngreso()));
            ps.setDate(4, Date.valueOf(reserva.getFechaSalida()));
            ps.setString(5, reserva.getEstado());
            ps.setInt(6, reserva.getIdReserva());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean cancelar(int idReserva) {
        String sql = "UPDATE reservas SET estado='cancelada' WHERE id_reserva=?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    private Reserva mapearReserva(ResultSet rs) throws SQLException {
        Clientes cliente = new Clientes();
        cliente.setIdCliente(rs.getInt("c.id_cliente"));
        cliente.setNombre(rs.getString("c.nombre"));
        cliente.setApellido(rs.getString("c.apellido"));
        cliente.setDni(rs.getString("c.dni"));
        cliente.setEmail(rs.getString("c.email"));
        cliente.setTelefono(rs.getString("c.telefono"));
        
        Habitacion habitacion = new Habitacion();
        habitacion.setIdHabitacion(rs.getInt("h.id_habitacion"));
        habitacion.setNumero(rs.getInt("h.numero"));
        habitacion.setTipo(rs.getString("h.tipo"));
        habitacion.setPrecio(rs.getDouble("h.precio"));
        
        Reserva reserva = new Reserva();
        reserva.setIdReserva(rs.getInt("r.id_reserva"));
        reserva.setCliente(cliente);
        reserva.setHabitacion(habitacion);
        reserva.setFechaIngreso(rs.getDate("r.fecha_ingreso").toLocalDate());
        reserva.setFechaSalida(rs.getDate("r.fecha_salida").toLocalDate());
        reserva.setEstado(rs.getString("r.estado"));
        return reserva;
    }
    public long contarReservasActivas() {
    String sql = "SELECT COUNT(*) FROM reservas WHERE estado = 'activa'";
    try (Connection conn = ConexionDB.conectar();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getLong(1);
    } catch (SQLException e) { e.printStackTrace(); }
    return 0;
}


    public long contarHabitacionesOcupadas() {
    String sql = "SELECT COUNT(DISTINCT id_habitacion) FROM reservas WHERE estado = 'activa'";
    try (Connection conn = ConexionDB.conectar();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getLong(1);
    } catch (SQLException e) { e.printStackTrace(); }
    return 0;
    }
}