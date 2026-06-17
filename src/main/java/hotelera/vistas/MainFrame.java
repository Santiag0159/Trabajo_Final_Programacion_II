package hotelera.vistas;

import javax.swing.*;
import java.awt.*;
import hotelera.bd.*;
import hotelera.modelos.*;
import java.util.List;

public class MainFrame extends JFrame {
    private CardLayout cardLayout;
    private JPanel panelContenido;
    private Usuario usuarioActual;  // usuario logueado

    public MainFrame(Usuario usuario) {
        this.usuarioActual = usuario;
        setTitle("Hotelera - Sistema de Gestión");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));
        initUI();
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Barra lateral
        JPanel sidebar = crearSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);
        
        // Panel contenido con CardLayout
        cardLayout = new CardLayout();
        panelContenido = new JPanel(cardLayout);
        panelContenido.setBackground(new Color(245, 245, 245));
        
        // Agregar paneles
        panelContenido.add(new InicioPanel(), "inicio");
        panelContenido.add(new ReservaPanel(), "reservas");
        
        // Solo si es administrador, agregar Reportes
        if (usuarioActual != null && "administrador".equals(usuarioActual.getRol())) {
            panelContenido.add(new ReportesPanel(), "reportes");
        }
        
        panelContenido.add(new HabitacionesPanel(), "habitaciones");
        
        mainPanel.add(panelContenido, BorderLayout.CENTER);
        add(mainPanel);
        
        // Mostrar inicio por defecto
        cardLayout.show(panelContenido, "inicio");
    }
    
    private JPanel crearSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(44, 62, 80));
        sidebar.setPreferredSize(new Dimension(250, getHeight()));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        
        JLabel lblTitulo = new JLabel("HOTELERA");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblTitulo);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));
        
        // Opciones del menú: Inicio, Reservas, Reportes (si admin), Búsqueda, Habitaciones
        String[] opciones;
        String[] cardNames;
        if (usuarioActual != null && "administrador".equals(usuarioActual.getRol())) {
            opciones = new String[]{"Inicio", "Reservas", "Reportes", "Búsqueda", "Habitaciones"};
            cardNames = new String[]{"inicio", "reservas", "reportes", "busqueda", "habitaciones"};
        } else {
            opciones = new String[]{"Inicio", "Reservas", "Búsqueda", "Habitaciones"};
            cardNames = new String[]{"inicio", "reservas", "busqueda", "habitaciones"};
        }
        
        for (int i = 0; i < opciones.length; i++) {
            JButton btn = crearBotonSidebar(opciones[i]);
            final String cardName = cardNames[i];
            btn.addActionListener(e -> cardLayout.show(panelContenido, cardName));
            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        sidebar.add(Box.createVerticalGlue());
        
        // Mostrar nombre de usuario
        JLabel lblUser = new JLabel("👤 " + usuarioActual.getNombreUsuario());
        lblUser.setForeground(Color.LIGHT_GRAY);
        lblUser.setFont(new Font("Arial", Font.PLAIN, 12));
        lblUser.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(lblUser);
        sidebar.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JButton btnLogout = crearBotonSidebar("Cerrar sesión");
        btnLogout.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
        sidebar.add(btnLogout);
        
        return sidebar;
    }
    
    private JButton crearBotonSidebar(String texto) {
        JButton btn = new JButton(texto);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setBackground(new Color(52, 73, 94));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("Arial", Font.PLAIN, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(41, 128, 185));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(52, 73, 94));
            }
        });
        return btn;
    }
    
    // ==================== PANELES ====================
    
    class InicioPanel extends JPanel {
        public InicioPanel() {
            setLayout(new GridBagLayout());
            setBackground(new Color(245, 245, 245));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(15,15,15,15);
            gbc.gridx = 0; gbc.gridy = 0;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            
            // Obtener datos reales
            ReservaDAO reservaDAO = new ReservaDAO();
            HabitacionDAO habDAO = new HabitacionDAO();
            long reservasActivas = reservaDAO.contarReservasActivas(); // nuevo método
            long habitacionesOcupadas = reservaDAO.contarHabitacionesOcupadas(); // nuevo método
            long totalHabitaciones = habDAO.contarTotalHabitaciones(); // nuevo método
            long disponibles = totalHabitaciones - habitacionesOcupadas;
            
            add(crearTarjeta("Reservas activas", String.valueOf(reservasActivas), new Color(52, 152, 219)), gbc);
            gbc.gridx = 1;
            add(crearTarjeta("Habitaciones ocupadas", String.valueOf(habitacionesOcupadas), new Color(46, 204, 113)), gbc);
            gbc.gridx = 2;
            add(crearTarjeta("Habitaciones disponibles", String.valueOf(disponibles), new Color(241, 176, 26)), gbc);
            
            // Tabla de próximas reservas
            gbc.gridx = 0; gbc.gridy = 1;
            gbc.gridwidth = 3;
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1;
            gbc.weighty = 1;
            add(crearTablaProximasReservas(), gbc);
        }
        
        private JPanel crearTarjeta(String titulo, String valor, Color color) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(color);
            card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            card.setPreferredSize(new Dimension(200, 100));
            JLabel lblTitulo = new JLabel(titulo);
            lblTitulo.setForeground(Color.WHITE);
            lblTitulo.setFont(new Font("Arial", Font.PLAIN, 14));
            JLabel lblValor = new JLabel(valor);
            lblValor.setForeground(Color.WHITE);
            lblValor.setFont(new Font("Arial", Font.BOLD, 28));
            lblValor.setHorizontalAlignment(SwingConstants.RIGHT);
            card.add(lblTitulo, BorderLayout.NORTH);
            card.add(lblValor, BorderLayout.CENTER);
            return card;
        }
        
        private JScrollPane crearTablaProximasReservas() {
            String[] columnas = {"ID", "Cliente", "Habitación", "Ingreso", "Salida", "Estado"};
            Object[][] datos = obtenerDatosReservas();
            JTable tabla = new JTable(datos, columnas);
            tabla.setFillsViewportHeight(true);
            tabla.setRowHeight(30);
            JScrollPane scroll = new JScrollPane(tabla);
            scroll.setBorder(BorderFactory.createTitledBorder("Próximas reservas"));
            return scroll;
        }
        
        private Object[][] obtenerDatosReservas() {
            ReservaDAO dao = new ReservaDAO();
            List<Reserva> reservas = dao.listarTodas();
            Object[][] data = new Object[reservas.size()][6];
            for (int i = 0; i < reservas.size(); i++) {
                Reserva r = reservas.get(i);
                data[i][0] = r.getIdReserva();
                data[i][1] = r.getCliente().getNombre() + " " + r.getCliente().getApellido();
                data[i][2] = r.getHabitacion().getNumero();
                data[i][3] = r.getFechaIngreso();
                data[i][4] = r.getFechaSalida();
                data[i][5] = r.getEstado();
            }
            return data;
        }
    }
    
    class ReservaPanel extends JPanel {
        public ReservaPanel() {
            setLayout(new BorderLayout());
            // Incrustar el formulario de reserva (convertido a JPanel)
            add(new ReservaFormPanel(), BorderLayout.CENTER);
        }
    }
    
    class HabitacionesPanel extends JPanel {
    private JPanel gridPanel;
    private JLabel lblEstado;

    public HabitacionesPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panel superior con título y leyenda
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(245, 245, 245));
        JLabel titulo = new JLabel("Planta de Habitaciones - Estado Actual");
        titulo.setFont(new Font("Arial", Font.BOLD, 18));
        topPanel.add(titulo, BorderLayout.WEST);

        // Leyenda de colores
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        leyenda.setBackground(new Color(245, 245, 245));
        leyenda.add(crearLeyenda("Disponible", new Color(46, 204, 113)));
        leyenda.add(crearLeyenda("Ocupada", new Color(231, 76, 60)));
        topPanel.add(leyenda, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // Panel central con las habitaciones
        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(0, 5, 10, 10)); // 5 columnas, filas automáticas
        gridPanel.setBackground(new Color(245, 245, 245));
        JScrollPane scroll = new JScrollPane(gridPanel);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        // Cargar habitaciones
        cargarHabitaciones();
    }

    private JPanel crearLeyenda(String texto, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        item.setBackground(new Color(245, 245, 245));
        JLabel colorBox = new JLabel("   ");
        colorBox.setOpaque(true);
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        colorBox.setPreferredSize(new Dimension(20, 20));
        JLabel label = new JLabel(texto);
        item.add(colorBox);
        item.add(label);
        return item;
    }

    private void cargarHabitaciones() {
        gridPanel.removeAll();
        HabitacionDAO habDao = new HabitacionDAO();
        ReservaDAO reservaDao = new ReservaDAO();
        List<Habitacion> habitaciones = habDao.listarTodas();
        List<Integer> ocupadasHoy = reservaDao.getHabitacionesOcupadasHoy();

        for (Habitacion h : habitaciones) {
            boolean ocupada = ocupadasHoy.contains(h.getIdHabitacion());
            JPanel card = crearCard(h, ocupada);
            gridPanel.add(card);
        }
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private JPanel crearCard(Habitacion h, boolean ocupada) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(ocupada ? new Color(231, 76, 60) : new Color(46, 204, 113));
        card.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
        card.setPreferredSize(new Dimension(100, 100));

        // Número de habitación (grande, centrado)
        JLabel numero = new JLabel(String.valueOf(h.getNumero()));
        numero.setFont(new Font("Arial", Font.BOLD, 28));
        numero.setForeground(Color.WHITE);
        numero.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(numero, BorderLayout.CENTER);

        // Tipo de habitación (abajo, pequeño)
        JLabel tipo = new JLabel(h.getTipo());
        tipo.setFont(new Font("Arial", Font.PLAIN, 12));
        tipo.setForeground(Color.WHITE);
        tipo.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(tipo, BorderLayout.SOUTH);

        // Tooltip con más info
        String estadoTexto = ocupada ? "Ocupada" : "Disponible";
        card.setToolTipText("Habitación " + h.getNumero() + " - " + h.getTipo() + " - " + estadoTexto + " ($" + h.getPrecio() + "/noche)");

        // Evento de clic para mostrar detalles
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JOptionPane.showMessageDialog(HabitacionesPanel.this,
                    "Habitación " + h.getNumero() + "\nTipo: " + h.getTipo() +
                    "\nPrecio: $" + h.getPrecio() + "\nEstado: " + estadoTexto);
            }
        });

        return card;
    }
}
    
    // ReportesPanel (solo visible para admin)
    class ReportesPanel extends JPanel {
        public ReportesPanel() {
            setLayout(new BorderLayout());
            add(new JLabel("Reportes - En construcción"), BorderLayout.CENTER);
        }
    }
}