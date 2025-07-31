package VIEW;

import DaoCV.EstudianteDAO;
import ModelCV.Estudiante;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EstudiantePanel extends JPanel {
    private JTable tblEstudiantes;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnUpdate, btnDelete;
    private EstudianteDAO estudianteDAO;

    // Date format for display and parsing
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * Constructs an EstudiantesPanel.
     */
    public EstudiantePanel() {
        estudianteDAO = new EstudianteDAO();
        setLayout(new BorderLayout(10, 10)); // Spacing

        // Table Model
        String[] columnNames = {"ID", "Nombre", "Apellido", "Fecha Nacimiento", "Dirección", "Teléfono", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int intColumn) {
                return false;
            }
        };
        tblEstudiantes = new JTable(tableModel);
        tblEstudiantes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tblEstudiantes);
        add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdd = new JButton("Añadir Estudiante");
        btnUpdate = new JButton("Modificar Estudiante");
        btnDelete = new JButton("Eliminar Estudiante");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);

        add(buttonPanel, BorderLayout.SOUTH);

        // Load initial data
        loadEstudiantes();

        // --- Button Actions ---

        // Add Student Button
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JTextField txtNombre = new JTextField();
                JTextField txtApellido = new JTextField();
                JDateChooser dateChooser = new JDateChooser(); // Date picker
                dateChooser.setDateFormatString("yyyy-MM-dd"); // Set date format for display in chooser
                JTextField txtDireccion = new JTextField();
                JTextField txtTelefono = new JTextField();
                JTextField txtEmail = new JTextField();

                JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
                inputPanel.add(new JLabel("Nombre:"));
                inputPanel.add(txtNombre);
                inputPanel.add(new JLabel("Apellido:"));
                inputPanel.add(txtApellido);
                inputPanel.add(new JLabel("Fecha Nacimiento:"));
                inputPanel.add(dateChooser); // Add date picker
                inputPanel.add(new JLabel("Dirección:"));
                inputPanel.add(txtDireccion);
                inputPanel.add(new JLabel("Teléfono:"));
                inputPanel.add(txtTelefono);
                inputPanel.add(new JLabel("Email:"));
                inputPanel.add(txtEmail);

                inputPanel.setPreferredSize(new Dimension(450, 250)); // Adjust size for more fields

                int result = JOptionPane.showConfirmDialog(EstudiantePanel.this, inputPanel, "Añadir Nuevo Estudiante", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        String nombre = txtNombre.getText().trim();
                        String apellido = txtApellido.getText().trim();
                        Date fechaNacimiento = dateChooser.getDate(); // Get date from JDateChooser
                        String direccion = txtDireccion.getText().trim();
                        String telefono = txtTelefono.getText().trim();
                        String email = txtEmail.getText().trim();

                        if (nombre.isEmpty() || apellido.isEmpty() || fechaNacimiento == null || email.isEmpty()) {
                            JOptionPane.showMessageDialog(EstudiantePanel.this, "Nombre, Apellido, Fecha de Nacimiento y Email son campos obligatorios.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) { // Simple email regex
                            JOptionPane.showMessageDialog(EstudiantePanel.this, "Por favor, ingrese un formato de email válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        Estudiante nuevoEstudiante = new Estudiante(0, nombre, apellido, fechaNacimiento, direccion, telefono, email);
                        estudianteDAO.addEstudiante(nuevoEstudiante);
                        JOptionPane.showMessageDialog(EstudiantePanel.this, "Estudiante añadido con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        loadEstudiantes(); // Reload table
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(EstudiantePanel.this, "Error al añadir estudiante: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Update Student Button
        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tblEstudiantes.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(EstudiantePanel.this, "Por favor, seleccione un estudiante para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int estudianteId = (int) tableModel.getValueAt(selectedRow, 0);
                String nombreActual = (String) tableModel.getValueAt(selectedRow, 1);
                String apellidoActual = (String) tableModel.getValueAt(selectedRow, 2);
                Date fechaNacimientoActual = null;
                try {
                    fechaNacimientoActual = dateFormat.parse((String) tableModel.getValueAt(selectedRow, 3));
                } catch (ParseException ex) {
                    System.err.println("Error parsing date from table: " + ex.getMessage());
                }
                String direccionActual = (String) tableModel.getValueAt(selectedRow, 4);
                String telefonoActual = (String) tableModel.getValueAt(selectedRow, 5);
                String emailActual = (String) tableModel.getValueAt(selectedRow, 6);

                JTextField txtNombre = new JTextField(nombreActual);
                JTextField txtApellido = new JTextField(apellidoActual);
                JDateChooser dateChooser = new JDateChooser(fechaNacimientoActual);
                dateChooser.setDateFormatString("yyyy-MM-dd");
                JTextField txtDireccion = new JTextField(direccionActual);
                JTextField txtTelefono = new JTextField(telefonoActual);
                JTextField txtEmail = new JTextField(emailActual);

                JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
                inputPanel.add(new JLabel("Nombre:"));
                inputPanel.add(txtNombre);
                inputPanel.add(new JLabel("Apellido:"));
                inputPanel.add(txtApellido);
                inputPanel.add(new JLabel("Fecha Nacimiento:"));
                inputPanel.add(dateChooser);
                inputPanel.add(new JLabel("Dirección:"));
                inputPanel.add(txtDireccion);
                inputPanel.add(new JLabel("Teléfono:"));
                inputPanel.add(txtTelefono);
                inputPanel.add(new JLabel("Email:"));
                inputPanel.add(txtEmail);

                inputPanel.setPreferredSize(new Dimension(450, 250));

                int result = JOptionPane.showConfirmDialog(EstudiantePanel.this, inputPanel, "Modificar Estudiante", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        String nombre = txtNombre.getText().trim();
                        String apellido = txtApellido.getText().trim();
                        Date fechaNacimiento = dateChooser.getDate();
                        String direccion = txtDireccion.getText().trim();
                        String telefono = txtTelefono.getText().trim();
                        String email = txtEmail.getText().trim();

                        if (nombre.isEmpty() || apellido.isEmpty() || fechaNacimiento == null || email.isEmpty()) {
                            JOptionPane.showMessageDialog(EstudiantePanel.this, "Nombre, Apellido, Fecha de Nacimiento y Email son campos obligatorios.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                            JOptionPane.showMessageDialog(EstudiantePanel.this, "Por favor, ingrese un formato de email válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        Estudiante estudianteModificado = new Estudiante(estudianteId, nombre, apellido, fechaNacimiento, direccion, telefono, email);
                        estudianteDAO.updateEstudiante(estudianteModificado);
                        JOptionPane.showMessageDialog(EstudiantePanel.this, "Estudiante modificado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        loadEstudiantes(); // Reload table
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(EstudiantePanel.this, "Error al modificar estudiante: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        // Delete Student Button
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tblEstudiantes.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(EstudiantePanel.this, "Por favor, seleccione un estudiante para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(EstudiantePanel.this, "¿Está seguro de que desea eliminar este estudiante?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int estudianteId = (int) tableModel.getValueAt(selectedRow, 0);
                    try {
                        estudianteDAO.deleteEstudiante(estudianteId);
                        JOptionPane.showMessageDialog(EstudiantePanel.this, "Estudiante eliminado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        loadEstudiantes(); // Reload table
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(EstudiantePanel.this, "Error al eliminar estudiante: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    /**
     * Loads all students from the database and populates the table model.
     */
    private void loadEstudiantes() {
        tableModel.setRowCount(0); // Clear the table
        List<Estudiante> estudiantes = estudianteDAO.getAllEstudiantes();
        for (Estudiante estudiante : estudiantes) {
            tableModel.addRow(new Object[]{
                    estudiante.getId(),
                    estudiante.getNombre(),
                    estudiante.getApellido(),
                    dateFormat.format(estudiante.getFechaNacimiento()), // Format date for display
                    estudiante.getDireccion(),
                    estudiante.getTelefono(),
                    estudiante.getEmail()
            });
        }

    }
}
