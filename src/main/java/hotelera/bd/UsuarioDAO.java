package hotelera.bd;

import hotelera.modelos.Usuario;
import java.sql.*;

public class UsuarioDAO {
    
    public Usuario validarLogin(String nombreUsuario, String contrasena) {
        String sql = "SELECT * FROM usuarios WHERE nombre_usuario = ? AND contrasena = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ps.setString(2, contrasena);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombreUsuario(rs.getString("nombre_usuario"));
                u.setContrasena(rs.getString("contrasena"));
                u.setRol(rs.getString("rol"));
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public boolean crearUsuario(Usuario usuario) {
    String sql = "INSERT INTO usuarios (nombre_usuario, contrasena, rol)"
            + " VALUES (?, ?, ?)";
    try (Connection conn = ConexionDB.conectar();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, usuario.getNombreUsuario());
        ps.setString(2, usuario.getContrasena());
        ps.setString(3, usuario.getRol());
        int filas = ps.executeUpdate();
        return filas > 0;
    } catch (SQLException e) {
        System.out.println("Error al crear usuario: " + e.getMessage());
        return false;
    }
}
    
    public Usuario obtenerPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";
        try (Connection conn = ConexionDB.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setNombreUsuario(rs.getString("nombre_usuario"));
                u.setContrasena(rs.getString("contrasena"));
                u.setRol(rs.getString("rol"));
                return u;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}