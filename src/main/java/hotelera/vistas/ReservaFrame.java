package hotelera.vistas;

import hotelera.bd.*;
import hotelera.modelos.*;
import hotelera.util.Calculadorprecio;
import com.toedter.calendar.JDateChooser;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

public class ReservaFrame extends JFrame {
    private JTextField txtDni, txtNombre, txtApellido, txtEmail, txtTelefono;
    private JComboBox<String> cbHabitacion;
    private JDateChooser dcIngreso, dcSalida;
    private JLabel lblTotal;
    private JButton btnCalcular, btnGuardar;
    private HabitacionDAO habDao;
    private ClienteDAO clienteDao;
    private ReservaDAO reservaDao;
    private List<Habitacion> habitacionesDisponibles;

    public ReservaFrame() {
        setTitle("Nueva Reserva");
        setSize(500, 500);
        setLocationRelativeTo(null);
        habDao = new HabitacionDAO();
        clienteDao = new ClienteDAO();
        reservaDao = new ReservaDAO();
        iniciarComponentes();
        cargarHabitacionesDisponibles();
    }

    private void iniciarComponentes() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.anchor = GridBagConstraints.WEST;

        int y = 0;
        addLabelAndField("DNI:", txtDni = new JTextField(15), gbc, y++);
        addLabelAndField("Nombre:", txtNombre = new JTextField(15), gbc, y++);
        addLabelAndField("Apellido:", txtApellido = new JTextField(15), gbc, y++);
        addLabelAndField("Email:", txtEmail = new JTextField(15), gbc, y++);
        addLabelAndField("Teléfono:", txtTelefono = new JTextField(15), gbc, y++);

        gbc.gridx = 0; gbc.gridy = y;
        add(new JLabel("Habitación:"), gbc);
        cbHabitacion = new JComboBox<>();
        gbc.gridx = 1;
        add(cbHabitacion, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        add(new JLabel("Fecha ingreso:"), gbc);
        dcIngreso = new JDateChooser();
        dcIngreso.setPreferredSize(new Dimension(150,25));
        gbc.gridx = 1;
        add(dcIngreso, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        add(new JLabel("Fecha salida:"), gbc);
        dcSalida = new JDateChooser();
        dcSalida.setPreferredSize(new Dimension(150,25));
        gbc.gridx = 1;
        add(dcSalida, gbc);
        y++;

        btnCalcular = new JButton("Calcular total");
        gbc.gridx = 0; gbc.gridy = y;
        gbc.gridwidth = 2;
        add(btnCalcular, gbc);
        y++;

        lblTotal = new JLabel("Total: $0.00");
        gbc.gridy = y;
        add(lblTotal, gbc);
        y++;

        btnGuardar = new JButton("Guardar reserva");
        gbc.gridy = y;
        add(btnGuardar, gbc);

        btnCalcular.addActionListener(e -> calcular());
        btnGuardar.addActionListener(e -> guardar());
    }

    private void addLabelAndField(String label, JTextField field, GridBagConstraints gbc, int y) {
        gbc.gridx = 0; gbc.gridy = y;
        add(new JLabel(label), gbc);
        gbc.gridx = 1;
        add(field, gbc);
    }

    private void cargarHabitacionesDisponibles() {
        // Al abrir, mostramos disponibilidad desde hoy +1 día por 2 días de ejemplo
        LocalDate ingreso = LocalDate.now().plusDays(1);
        LocalDate salida = ingreso.plusDays(2);
        habitacionesDisponibles = habDao.listarDisponibles(ingreso, salida);
        cbHabitacion.removeAllItems();
        for (Habitacion h : habitacionesDisponibles) {
            cbHabitacion.addItem(h.getNumero() + " - " + h.getTipo() + " ($" + h.getPrecio() + ")");
        }
    }

    private void calcular() {
        if (dcIngreso.getDate() == null || dcSalida.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione ambas fechas");
            return;
        }
        LocalDate ingreso = dcIngreso.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate salida = dcSalida.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        if (salida.isBefore(ingreso) || salida.equals(ingreso)) {
            JOptionPane.showMessageDialog(this, "Fecha de salida debe ser posterior a ingreso");
            return;
        }
        int idx = cbHabitacion.getSelectedIndex();
        if (idx < 0) return;
        Habitacion h = habitacionesDisponibles.get(idx);
        long dias = Calculadorprecio.calcularDias(ingreso, salida);
        double total = Calculadorprecio.calcularTotal(dias, h.getPrecio());
        lblTotal.setText(String.format("Total: $%.2f", total));
    }

    private void guardar() {
        try {
            String dni = txtDni.getText().trim();
            if (dni.isEmpty()) throw new Exception("DNI obligatorio");
            Clientes cliente = clienteDao.buscarPorDni(dni);
            if (cliente == null) {
                cliente = new Clientes();
                cliente.setDni(dni);
                cliente.setNombre(txtNombre.getText().trim());
                cliente.setApellido(txtApellido.getText().trim());
                cliente.setEmail(txtEmail.getText().trim());
                cliente.setTelefono(txtTelefono.getText().trim());
                if (!clienteDao.guardar(cliente)) throw new Exception("Error al guardar cliente");
            }
            int idx = cbHabitacion.getSelectedIndex();
            if (idx < 0) throw new Exception("Seleccione una habitación");
            Habitacion hab = habitacionesDisponibles.get(idx);
            LocalDate ingreso = dcIngreso.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate salida = dcSalida.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Reserva reserva = new Reserva();
            reserva.setCliente(cliente);
            reserva.setHabitacion(hab);
            reserva.setFechaIngreso(ingreso);
            reserva.setFechaSalida(salida);
            reserva.setEstado("activa");
            if (reservaDao.guardar(reserva)) {
                JOptionPane.showMessageDialog(this, "Reserva guardada con ID " + reserva.getIdReserva());
                limpiarFormulario();
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar reserva (posible conflicto de disponibilidad)");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtDni.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtEmail.setText("");
        txtTelefono.setText("");
        dcIngreso.setDate(null);
        dcSalida.setDate(null);
        lblTotal.setText("Total: $0.00");
        cargarHabitacionesDisponibles();
    }
}