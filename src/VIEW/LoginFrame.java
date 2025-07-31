package VIEW;

import DaoCV.UsuarioDAO;
import ModelCV.Usuario;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private UsuarioDAO usuarioDAO;

    public LoginFrame() {
        super("Login - Cursos Vacacionales");
        usuarioDAO = new UsuarioDAO();

        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblTitle = new JLabel("Iniciar Sesión", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitle, gbc);

        JLabel lblUsername = new JLabel("Usuario:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(lblUsername, gbc);

        txtUsername = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(txtUsername, gbc);

        JLabel lblPassword = new JLabel("Contraseña:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(lblPassword, gbc);

        txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2;
        panel.add(txtPassword, gbc);

        btnLogin = new JButton("Ingresar");
        btnLogin.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(btnLogin, gbc);

        // Acción del botón Ingresar
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText();
                String password = new String(txtPassword.getPassword());

                Usuario usuario = usuarioDAO.autenticar(username, password);

                if (usuario != null) {
                    JOptionPane.showMessageDialog(LoginFrame.this, "¡Bienvenido, " + usuario.getUsername() + "!", "Login Exitoso", JOptionPane.INFORMATION_MESSAGE);

                    MainFrame mainFrame = new MainFrame(usuario.getRol());
                    mainFrame.setVisible(true);

                    // Cambiar de pestaña automáticamente según el rol
                    String rol = usuario.getRol().toLowerCase();
                    JTabbedPane tabs = mainFrame.getTabbedPane();

                    for (int i = 0; i < tabs.getTabCount(); i++) {
                        String title = tabs.getTitleAt(i).toLowerCase();

                        if (rol.equals("admin") && title.contains("horario")) {
                            tabs.setSelectedIndex(i); // Ir a Gestión de Horarios
                            break;
                        } else if (!rol.equals("admin") && title.contains("inscripcion")) {
                            tabs.setSelectedIndex(i); // Ir a Mis Inscripciones
                            break;
                        }
                    }

                    dispose(); // Cerrar Login
                } else {
                    JOptionPane.showMessageDialog(LoginFrame.this, "Usuario o contraseña incorrectos.", "Error de Login", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
