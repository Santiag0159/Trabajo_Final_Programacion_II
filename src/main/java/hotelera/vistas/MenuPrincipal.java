package hotelera.vistas;

import javax.swing.*;
import java.awt.*;

public class MenuPrincipal extends JFrame{
    private JButton btnHabitaciones, btnReservas, btnResumen, btnReporte;
    
    public MenuPrincipal(){
        setTitle("Sistema de gestion hotelera");
        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        setLayout(new BorderLayout());
        
        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new GridLayout(4,1,10,10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        
        btnHabitaciones = new JButton("Habitaciones");
        btnReservas = new JButton("Reservas");
        btnResumen = new JButton("Resumen");
        btnReporte = new JButton("Reportes");
        
        panelBotones.add(btnHabitaciones);
        panelBotones.add(btnReservas);
        panelBotones.add(btnResumen);
        panelBotones.add(btnReporte);
        
        add(panelBotones, BorderLayout.EAST);
        
        JLabel lblBienvenida = new JLabel("Bienvenidos al sistema", SwingConstants.CENTER);
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 24));
        add(lblBienvenida, BorderLayout.CENTER);
        
        setVisible(true);
        
    }
    
}
