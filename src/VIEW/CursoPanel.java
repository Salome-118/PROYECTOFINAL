package VIEW;

import DaoCV.CursoDAO;
import ModelCV.Curso;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CursoPanel extends JPanel {
    private JTable tblCursos;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnUpdate, btnDelete;
    private CursoDAO cursoDAO;
    private String userRol; // Para habilitar/deshabilitar botones

    public CursoPanel(String rol) {
        this.userRol = rol;
        cursoDAO = new CursoDAO();
        setLayout(new BorderLayout(10, 10)); // Espaciado

        // Modelo de la tabla
        String[] columnNames = {"ID", "Nombre", "Descripción", "Costo"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer que las celdas no sean editables directamente
            }
        };
        tblCursos = new JTable(tableModel);
        tblCursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Solo una fila seleccionable
        JScrollPane scrollPane = new JScrollPane(tblCursos);
        add(scrollPane, BorderLayout.CENTER);

        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdd = new JButton("Añadir Curso");
        btnUpdate = new JButton("Modificar Curso");
        btnDelete = new JButton("Eliminar Curso");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);

        add(buttonPanel, BorderLayout.SOUTH);

        // Habilitar/deshabilitar botones según el rol
        boolean isAdmin = userRol.equals("admin");
        btnAdd.setVisible(isAdmin);
        btnUpdate.setVisible(isAdmin);
        btnDelete.setVisible(isAdmin);

        // Cargar datos iniciales
        loadCursos();

        // --- Acciones de los botones ---
        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Lógica para añadir un nuevo curso
                JTextField txtNombre = new JTextField();
                JTextField txtDescripcion = new JTextField();
                JTextField txtCosto = new JTextField();

                JPanel inputPanel = new JPanel(new GridLayout(0, 2));
                inputPanel.add(new JLabel("Nombre:"));
                inputPanel.add(txtNombre);
                inputPanel.add(new JLabel("Descripción:"));
                inputPanel.add(txtDescripcion);
                inputPanel.add(new JLabel("Costo:"));
                inputPanel.add(txtCosto);
                int result = JOptionPane.showConfirmDialog(CursoPanel.this, inputPanel, "Añadir Nuevo Curso", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        String nombre = txtNombre.getText();
                        String descripcion = txtDescripcion.getText();
                        double costo = Double.parseDouble(txtCosto.getText());

                        if (nombre.isEmpty() || descripcion.isEmpty() || costo < 0) {
                            JOptionPane.showMessageDialog(CursoPanel.this, "Por favor, complete todos los campos y asegúrese de que el costo sea válido.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        Curso nuevoCurso = new Curso(0, nombre, descripcion, costo);
                        cursoDAO.addCurso(nuevoCurso);
                        JOptionPane.showMessageDialog(CursoPanel.this, "Curso añadido con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        loadCursos(); // Recargar la tabla
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(CursoPanel.this, "El costo debe ser un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        btnUpdate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tblCursos.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(CursoPanel.this, "Por favor, seleccione un curso para modificar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                int cursoId = (int) tableModel.getValueAt(selectedRow, 0);
                String nombreActual = (String) tableModel.getValueAt(selectedRow, 1);
                String descripcionActual = (String) tableModel.getValueAt(selectedRow, 2);
                double costoActual = (double) tableModel.getValueAt(selectedRow, 3);

                JTextField txtNombre = new JTextField(nombreActual);
                JTextField txtDescripcion = new JTextField(descripcionActual);
                JTextField txtCosto = new JTextField(String.valueOf(costoActual));

                JPanel inputPanel = new JPanel(new GridLayout(0, 2));
                inputPanel.add(new JLabel("Nombre:"));
                inputPanel.add(txtNombre);
                inputPanel.add(new JLabel("Descripción:"));
                inputPanel.add(txtDescripcion);
                inputPanel.add(new JLabel("Costo:"));
                inputPanel.add(txtCosto);
                int result = JOptionPane.showConfirmDialog(CursoPanel.this, inputPanel, "Modificar Curso", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        String nombre = txtNombre.getText();
                        String descripcion = txtDescripcion.getText();
                        double costo = Double.parseDouble(txtCosto.getText());

                        if (nombre.isEmpty() || descripcion.isEmpty() || costo < 0) {
                            JOptionPane.showMessageDialog(CursoPanel.this, "Por favor, complete todos los campos y asegúrese de que el costo sea válido.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        Curso cursoModificado = new Curso(cursoId, nombre, descripcion, costo);
                        cursoDAO.updateCurso(cursoModificado);
                        JOptionPane.showMessageDialog(CursoPanel.this, "Curso modificado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        loadCursos(); // Recargar la tabla
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(CursoPanel.this, "El costo debe ser un número válido.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tblCursos.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(CursoPanel.this, "Por favor, seleccione un curso para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(CursoPanel.this, "¿Está seguro de que desea eliminar este curso?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int cursoId = (int) tableModel.getValueAt(selectedRow, 0);
                    cursoDAO.deleteCurso(cursoId);
                    JOptionPane.showMessageDialog(CursoPanel.this, "Curso eliminado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    loadCursos(); // Recargar la tabla
                }
            }
        });
    }
    private void loadCursos() {
        tableModel.setRowCount(0); // Limpiar la tabla
        List<Curso> cursos = cursoDAO.getAllCursos();
        for (Curso curso : cursos) {
            tableModel.addRow(new Object[]{curso.getId(), curso.getNombre(), curso.getDescripcion(), curso.getCosto()});
        }
    }

}