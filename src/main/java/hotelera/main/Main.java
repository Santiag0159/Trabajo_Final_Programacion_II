package hotelera.main;

import java.sql.Connection;
import java.sql.PreparedStatement;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        
        // 1. Le pedimos la conexión a nuestra clase MySQL
        Connection con = Conexion.obtenerConexion();
        
        // Si la conexión no es nula, significa que pudimos entrar a MySQL
        if (con != null) {
            System.out.println("Intentando leer la tabla de usuarios...");
            
            // 2. Definimos la consulta SQL tal cual te la piden en la facultad
            String sql = "SELECT * FROM usuarios"; 
            
            // Usamos un try-with-resources para que Java cierre todo automáticamente al terminar
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                System.out.println("\n=== USUARIOS ENCONTRADOS EN MYSQL ===");
                
                // 3. El ResultSet empieza ANTES de la primera fila. 
                // rs.next() mueve el "puntero" a la siguiente fila. Si hay datos, devuelve true.
                int contador = 0;
                while (rs.next()) {
                    contador++;
                    // Extraemos los datos usando el nombre exacto de las columnas en tu MySQL
                    int id = rs.getInt("id"); // Cambia "id" por tu columna clave
                    String username = rs.getString("username"); // Cambia por tu columna de usuario
                    
                    System.out.println("Registro " + contador + " -> ID: " + id + " | Usuario: " + username);
                }
                
                if (contador == 0) {
                    System.out.println("La conexión funcionó, pero la tabla 'usuarios' está vacía.");
                    System.out.println("Prueba agregar una fila a mano desde MySQL Workbench.");
                }
                
                // 4. Cerramos la conexión general
                con.close();
                
            } catch (SQLException e) {
                System.out.println("❌ Error al ejecutar el SELECT. ¿Seguro que la tabla se llama 'usuarios'?");
                e.printStackTrace();
            }
            
        } else {
            System.out.println("❌ No se pudo ejecutar la prueba porque la conexión es nula.");
        }
    }
}
