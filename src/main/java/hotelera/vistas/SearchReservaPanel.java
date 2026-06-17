package hotelera.vistas;

import hotelera.bd.*;
import hotelera.modelos.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class SearchReservaPanel extends JPanel {
    private JTextField txtDni, txtNombre, txtHabitacion;
    private JTable tblResultados;
    private DefaultTableModel modelo;
    private JButton btnEditar, btnCancelar, btnLimpiar;
    private ReservaDAO reservaDao;
    private ClienteDAO clienteDao;
    private List<Reserva> resultados;
    private Timer timerBusqueda;

    public SearchReservaPanel() {
        reservaDao = new ReservaDAO();
        clienteDao = new ClienteDAO();
        resultados = new ArrayList<>();
        
        inicializarComponentes();
        cargarTodasLasReservas();
    }

    private void inicializarComponentes() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        
        JPanel panelBusqueda = new JPanel(new GridBagLayout());
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Filtros de Búsqueda"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        panelBusqueda.add(new JLabel("DNI Cliente:"), gbc);
        gbc.gridx = 1;
        txtDni = new JTextField(15);
        panelBusqueda.add(txtDni, gbc);
        
        
        gbc.gridx = 2;
        panelBusqueda.add(new JLabel("Nombre/Apellido:"), gbc);
        gbc.gridx = 3;
        txtNombre = new JTextField(15);
        panelBusqueda.add(txtNombre, gbc);
        
        
        gbc.gridx = 4;
        panelBusqueda.add(new JLabel("Nº Habitación:"), gbc);
        gbc.gridx = 5;
        txtHabitacion = new JTextField(10);
        panelBusqueda.add(txtHabitacion, gbc);
        
        
        gbc.gridx = 6;
        btnLimpiar = new JButton("Limpiar");
        panelBusqueda.add(btnLimpiar, gbc);
        
        add(panelBusqueda, BorderLayout.NORTH);
        
        
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createTitledBorder("Resultados"));
        
        String[] columnas = {"ID", "DNI", "Nombre", "Habitación", "Tipo", "Fecha Ingreso", "Fecha Salida", "Estado"};
        modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblResultados = new JTable(modelo);
        tblResultados.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblResultados.getColumnModel().getColumn(0).setPreferredWidth(40);
        tblResultados.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblResultados.getColumnModel().getColumn(2).setPreferredWidth(120);
        
        JScrollPane scrollPane = new JScrollPane(tblResultados);
        panelTabla.add(scrollPane, BorderLayout.CENTER);
        
        add(panelTabla, BorderLayout.CENTER);
        
        
        JPanel panelBotones = new JPanel();
        btnEditar = new JButton("Editar");
        btnCancelar = new JButton("Cancelar Reserva");
        
        panelBotones.add(btnEditar);
        panelBotones.add(btnCancelar);
        
        add(panelBotones, BorderLayout.SOUTH);
        
        // Listeners para búsqueda dinámica
        txtDni.getDocument().addDocumentListener(new BuscadorListener());
        txtNombre.getDocument().addDocumentListener(new BuscadorListener());
        txtHabitacion.getDocument().addDocumentListener(new BuscadorListener());
        
        // Listeners de botones
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        btnEditar.addActionListener(e -> editarReserva());
        btnCancelar.addActionListener(e -> cancelarReserva());
    }

    private void cargarTodasLasReservas() {
        resultados = reservaDao.listarTodas();
        actualizarTabla(resultados);
    }

    private void actualizarTabla(List<Reserva> reservas) {
        modelo.setRowCount(0);
        for (Reserva r : reservas) {
            Object[] fila = {
                r.getIdReserva(),
                r.getCliente().getDni(),
                r.getCliente().getNombre() + " " + r.getCliente().getApellido(),
                r.getHabitacion().getNumero(),
                r.getHabitacion().getTipo(),
                r.getFechaIngreso(),
                r.getFechaSalida(),
                r.getEstado()
            };
            modelo.addRow(fila);
        }
    }

    public void realizarBusqueda() {
        String dni = txtDni.getText().trim();
        String nombre = txtNombre.getText().trim();
        String habitacionStr = txtHabitacion.getText().trim();
        
        Integer habitacion = null;
        if (!habitacionStr.isEmpty()) {
            try {
                habitacion = Integer.parseInt(habitacionStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El número de habitación debe ser numérico");
                return;
            }
        }
        
        List<Reserva> resultadosBusqueda = reservaDao.buscarCombinado(dni, nombre, habitacion);
        actualizarTabla(resultadosBusqueda);
        resultados = resultadosBusqueda;
    }

    private void limpiarFiltros() {
        txtDni.setText("");
        txtNombre.setText("");
        txtHabitacion.setText("");
        cargarTodasLasReservas();
    }

    private void editarReserva() {
        int selectedRow = tblResultados.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una reserva para editar");
            return;
        }
        
        int idReserva = (int) modelo.getValueAt(selectedRow, 0);
        Reserva reserva = reservaDao.obtenerPorId(idReserva);
        
        if (reserva != null) {
            EditarReservaFrame editFrame = new EditarReservaFrame(reserva);
            editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            editFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    realizarBusqueda();
                }
            });
            editFrame.setVisible(true);
        }
    }

    private void cancelarReserva() {
        int selectedRow = tblResultados.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una reserva para cancelar");
            return;
        }
        
        int idReserva = (int) modelo.getValueAt(selectedRow, 0);
        String estado = (String) modelo.getValueAt(selectedRow, 7);
        
        if ("cancelada".equalsIgnoreCase(estado)) {
            JOptionPane.showMessageDialog(this, "Esta reserva ya está cancelada");
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Confirma que desea cancelar esta reserva?",
            "Confirmar cancelación",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (reservaDao.cancelar(idReserva)) {
                JOptionPane.showMessageDialog(this, "Reserva cancelada exitosamente");
                realizarBusqueda();
            } else {
                JOptionPane.showMessageDialog(this, "Error al cancelar la reserva");
            }
        }
    }

    // Clase interna para escuchar cambios en los campos de búsqueda
    private class BuscadorListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            buscarConDelay();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            buscarConDelay();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            buscarConDelay();
        }

        private void buscarConDelay() {
            
            if (timerBusqueda != null) {
                timerBusqueda.stop();
            }
            timerBusqueda = new Timer(300, e -> {
                SwingUtilities.invokeLater(() -> realizarBusqueda());
            });
            timerBusqueda.setRepeats(false);
            timerBusqueda.start();
        }
    }
}
