package hotelera.vistas;

import hotelera.controladores.LoginController;
import hotelera.modelos.Usuario;   // ← IMPORTANTE
import javax.swing.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private LoginController controlador;

    public LoginFrame() {
        setTitle("Hotelera - Login");
        setSize(350, 230);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        iniciarComponentes();
        controlador = new LoginController(this);
    }

    private void iniciarComponentes() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setBounds(30, 30, 80, 25);
        txtUsuario = new JTextField();
        txtUsuario.setBounds(120, 30, 180, 25);

        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setBounds(30, 70, 80, 25);
        txtPassword = new JPasswordField();
        txtPassword.setBounds(120, 70, 180, 25);

        btnLogin = new JButton("Iniciar sesión");
        btnLogin.setBounds(120, 110, 150, 30);

        JButton btnRegistrar = new JButton("Registrarse");
        btnRegistrar.setBounds(120, 145, 150, 30);

        panel.add(lblUsuario);
        panel.add(txtUsuario);
        panel.add(lblPassword);
        panel.add(txtPassword);
        panel.add(btnLogin);
        panel.add(btnRegistrar);

        add(panel);

        btnRegistrar.addActionListener(e -> new RegistrarUsuario().setVisible(true));
    }

    public String getUsuario() { return txtUsuario.getText(); }
    public String getPassword() { return new String(txtPassword.getPassword()); }
    public JButton getBtnLogin() { return btnLogin; }
    public void mostrarMensaje(String msg) { JOptionPane.showMessageDialog(this, msg); }

    // Método modificado: recibe Usuario
    public void abrirMainFrame(Usuario usuario) {
        new MainFrame(usuario).setVisible(true);
        this.dispose();
    }
}