package hotelera.controladores;

import hotelera.bd.UsuarioDAO;
import hotelera.modelos.Usuario;
import hotelera.vistas.LoginFrame;

public class LoginController {
    private LoginFrame vista;
    private UsuarioDAO dao;

    public LoginController(LoginFrame vista) {
        this.vista = vista;
        this.dao = new UsuarioDAO();
        this.vista.getBtnLogin().addActionListener(e -> autenticar());
    }

    private void autenticar() {
        String user = vista.getUsuario();
        String pass = vista.getPassword();
        System.out.println("Intentando login: " + user);
        Usuario u = dao.validarLogin(user, pass);
        if (u != null) {
            vista.mostrarMensaje("Bienvenido " + u.getNombreUsuario());
            vista.abrirMainFrame(u);   // ← pasamos el objeto Usuario
        } else {
            vista.mostrarMensaje("Usuario o contraseña incorrectos");
        }
    }
}