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

public class EditarReservaFrame extends JFrame {
    private JTextField txtDni, txtNombre, txtApellido, txtEmail, txtTelefono;
    private JComboBox<String> cbHabitacion;
    private JDateChooser dcIngreso, dcSalida;
    private JLabel lblTotal;
    private JButton btnCalcular, btnGuardar;
    private HabitacionDAO habDao;
    private ClienteDAO clienteDao;
    private ReservaDAO reservaDao;
    private List<Habitacion> habitacionesDisponibles;
    private Reserva reservaActual;

public EditarReservaFrame(Reserva reserva) {
    this.reservaActual = reserva;
    setTitle("Editar Reserva #" + reserva.getIdReserva());
    setSize(500, 500);
    setLocationRelativeTo(null);
    habDao = new hotelera.bd.HabitacionDAO();
    clienteDao = new hotelera.bd.ClienteDAO();
    reservaDao = new hotelera.bd.ReservaDAO();
    
    iniciarComponentes(); // Esto carga la parte visual
    cargarHabitacionesDisponibles();
    
    // Agregamos a la lista la habitación que ya tiene asignada por si no quiere cambiarla
    cbHabitacion.addItem(reserva.getHabitacion().getNumero() + " - " + reserva.getHabitacion().getTipo() + " ($" + reserva.getHabitacion().getPrecio() + ")");
    cbHabitacion.setSelectedIndex(cbHabitacion.getItemCount() - 1); // La dejamos seleccionada por defecto

    // Rellenamos los campos con los datos actuales
    txtDni.setText(reserva.getCliente().getDni());
    txtNombre.setText(reserva.getCliente().getNombre());
    txtApellido.setText(reserva.getCliente().getApellido());
    txtEmail.setText(reserva.getCliente().getEmail());
    txtTelefono.setText(reserva.getCliente().getTelefono());
    
    // Setear fechas (requiere convertir LocalDate a Date para el JDateChooser)
    dcIngreso.setDate(java.util.Date.from(reserva.getFechaIngreso().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
    dcSalida.setDate(java.util.Date.from(reserva.getFechaSalida().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
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

                // Buscamos si existe
                Clientes cliente = clienteDao.buscarPorDni(dni);
                if (cliente == null) {
                    cliente = new Clientes();
                    cliente.setDni(dni);
                    cliente.setNombre(txtNombre.getText().trim());
                    cliente.setApellido(txtApellido.getText().trim());
                    cliente.setEmail(txtEmail.getText().trim());
                    cliente.setTelefono(txtTelefono.getText().trim());
                    if (!clienteDao.guardar(cliente)) throw new Exception("Error al guardar el nuevo cliente");
                } else {
                    // ACÁ ESTABA EL ERROR: Si el cliente ya existía, no guardábamos los cambios.
                    // Ahora forzamos a que el objeto tome los textos nuevos de la pantalla:
                    cliente.setNombre(txtNombre.getText().trim());
                    cliente.setApellido(txtApellido.getText().trim());
                    cliente.setEmail(txtEmail.getText().trim());
                    cliente.setTelefono(txtTelefono.getText().trim());
                    clienteDao.actualizar(cliente); // Actualizamos la base de datos
                }

                int idx = cbHabitacion.getSelectedIndex();
                if (idx < 0) throw new Exception("Seleccione una habitación");

                // Determinar qué habitación seleccionó
                Habitacion hab;
                if (idx == cbHabitacion.getItemCount() - 1) {
                    hab = reservaActual.getHabitacion(); // Dejó la que ya tenía
                } else {
                    hab = habitacionesDisponibles.get(idx); // Eligió una nueva de la lista
                }

                java.time.LocalDate ingreso = dcIngreso.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
                java.time.LocalDate salida = dcSalida.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

                // Modificamos el objeto reserva original
                reservaActual.setCliente(cliente);
                reservaActual.setHabitacion(hab);
                reservaActual.setFechaIngreso(ingreso);
                reservaActual.setFechaSalida(salida);

                // Mandamos a la base de datos
                if (reservaDao.actualizar(reservaActual)) { 
                    JOptionPane.showMessageDialog(this, "Reserva actualizada con éxito.");
                    this.dispose(); // Al cerrarse dispara el refresco de la tabla
                } else {
                    JOptionPane.showMessageDialog(this, "Error al actualizar la reserva.");
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