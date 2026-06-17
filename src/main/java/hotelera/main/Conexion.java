/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package hotelera.main;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Rodri
 */
public class Conexion {
    
    // 1. La URL ahora apunta al servidor local, puerto 3306, y al nombre de tu esquema
    private static final String URL = "jdbc:mysql://localhost:3306/sistema_hotel";
    
    // 2. El usuario por defecto de MySQL siempre es "root"
    private static final String USUARIO = "root"; 
    
    // 3. ¡MUY IMPORTANTE! Pon aquí la contraseña exacta que configuraste al instalar MySQL
    private static final String PASSWORD = ""; 

    public static Connection obtenerConexion() {
        Connection conexion = null;
        try {
            // Cargamos el driver de MySQL (el que agregamos al pom.xml)
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Intentamos conectar
            conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("¡Conexión exitosa a MySQL para el sistema de hoteles!");
            
        } catch (ClassNotFoundException e) {
            System.out.println("Error: No se encontró el conector de MySQL en el proyecto.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error de conexión: Verifica si el servicio de MySQL está encendido, o si la contraseña es correcta.");
            e.printStackTrace();
        }
        return conexion;
    }
}
