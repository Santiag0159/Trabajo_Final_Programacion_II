package hotelera.main;

import hotelera.vistas.LoginFrame;

public class Main {
    public static void main(String[] args) {
        System.out.println("Todo correcto, ejecutando :P ...");
        
        // Lanzar la interfaz grÃ¡fica en el hilo de eventos de Swing
        java.awt.EventQueue.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}