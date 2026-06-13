package hotelera.vistas;

import hotelera.bd.UsuarioDAO;
import hotelera.modelos.Usuario;
import javax.swing.*;
import java.awt.*;

public class RegistrarUsuario extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRol;
    private JButton btnRegistrar;

    public RegistrarUsuario() {
        setTitle("Registrar nueva cuenta");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setResizable(false);
        iniciarComponentes();
    }

    private void iniciarComponentes() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Usuario:"), gbc);
        txtUsuario = new JTextField(15);
        gbc.gridx = 1;
        panel.add(txtUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Contraseña:"), gbc);
        txtPassword = new JPasswordField(15);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Rol:"), gbc);
        cbRol = new JComboBox<>(new String[]{"recepcion", "administrador"});
        gbc.gridx = 1;
        panel.add(cbRol, gbc);

        btnRegistrar = new JButton("Registrarse");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnRegistrar, gbc);

        add(panel);

        btnRegistrar.addActionListener(e -> registrar());
    }

    private void registrar() {
        String usuario = txtUsuario.getText().trim();
        String pass = new String(txtPassword.getPassword()).trim();
        String rol = (String) cbRol.getSelectedItem();

        if (usuario.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos");
            return;
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombreUsuario(usuario);
        nuevo.setContrasena(pass);
        nuevo.setRol(rol);

        UsuarioDAO dao = new UsuarioDAO();
        if (dao.crearUsuario(nuevo)) {
            JOptionPane.showMessageDialog(this, "Usuario creado exitosamente."
                    + " Ahora puede iniciar sesión.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Error: el usuario ya existe"
                    + " o hubo un problema.");
        }
    }
}