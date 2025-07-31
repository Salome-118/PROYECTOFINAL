package VIEW;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JTabbedPane tabbedPane;
    private String userRol; // Para controlar qué paneles son visibles

    public MainFrame(String rol) {
        super("Gestión de Cursos Vacacionales - " + (rol.equals("admin") ? "Administrador" : "Estudiante"));
        this.userRol = rol;

        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar la ventana

        tabbedPane = new JTabbedPane();

        // Paneles comunes para ambos roles
        tabbedPane.addTab("Cursos Disponibles", new CursoPanel(rol));
        tabbedPane.addTab("Mis Inscripciones", new InscripcionPanel(rol));

        // Paneles solo para el rol admin
        if (userRol.equals("admin")) {
            tabbedPane.addTab("Gestión de Estudiantes", new EstudiantePanel());
            tabbedPane.addTab("Gestión de Horarios", new HorarioPanel());
        }

        add(tabbedPane, BorderLayout.CENTER);
    }

    // 🔓 Método público para acceder a las pestañas desde LoginFrame
    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }
}
