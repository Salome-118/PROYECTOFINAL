package VIEW;

import DaoCV.EstudianteDAO;
import DaoCV.HorarioDAO;
import DaoCV.CursoDAO;
import DaoCV.InscripcionDAO;
import ModelCV.Estudiante;
import ModelCV.Horario;
import ModelCV.Curso;
import ModelCV.Inscripcion;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.regex.Pattern;

public class InscripcionPanel extends JPanel  {

    // Tabla central de TODAS las inscripciones
    private JTable tblInscripciones;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnDelete;
    private InscripcionDAO inscripcionDAO;
    private EstudianteDAO estudianteDAO;
    private HorarioDAO horarioDAO;
    private String userRol;

    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private JTable table1;

    //  Horarios disponibles + buscador
    private JTable tblHorariosDisp;
    private DefaultTableModel modeloHorariosDisp;
    private JTextField txtBuscarCurso;
    private TableRowSorter<DefaultTableModel> sorterHorarios;
    private CursoDAO cursoDAO;
    private Map<Integer, String> cursosPorId = new HashMap<>();

    // Panel derecho: Mis Inscripciones
    private JTable tblMisInscripciones;
    private DefaultTableModel modeloMisInscripciones;

    //  Botones para estudiante
    private JButton btnInscribirme, btnVerMis, btnRetirarme;

    // Estudiante actual
    private Integer estudianteActualId = null;

    //  "Mis inscripciones"
    private JDialog dlgMisInscripciones;
    private JTable tblMisInsDialog;
    private DefaultTableModel modeloMisInsDialog;

    /**
     * Constructs an InscripcionesPanel.
     * @param rol The role of the logged-in user ('admin' or 'estudiante').
     */
    public InscripcionPanel(String rol) {
        this.userRol = rol;
        inscripcionDAO = new InscripcionDAO();
        estudianteDAO = new EstudianteDAO();
        horarioDAO = new HorarioDAO();
        cursoDAO = new CursoDAO();

        setLayout(new BorderLayout(10, 10)); // Spacing

        // Horarios disponibles
        JPanel panelHorarios = new JPanel(new BorderLayout(5,5));
        panelHorarios.setBorder(BorderFactory.createTitledBorder("Horarios Disponibles"));

        JPanel barraBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        barraBusqueda.add(new JLabel("Buscar curso:"));
        txtBuscarCurso = new JTextField(22);
        barraBusqueda.add(txtBuscarCurso);
        panelHorarios.add(barraBusqueda, BorderLayout.NORTH);


        modeloHorariosDisp = new DefaultTableModel(new Object[]{"ID Horario","Curso","Día","Inicio","Fin"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblHorariosDisp = new JTable(modeloHorariosDisp);
        sorterHorarios = new TableRowSorter<>(modeloHorariosDisp);
        tblHorariosDisp.setRowSorter(sorterHorarios);


        txtBuscarCurso.getDocument().addDocumentListener(new DocumentListener() {
            private void filtrar() {
                String q = txtBuscarCurso.getText().trim();
                if (q.isEmpty()) {
                    sorterHorarios.setRowFilter(null);
                } else {
                    sorterHorarios.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(q), 1));
                }
            }
            @Override public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override public void changedUpdate(DocumentEvent e) { filtrar(); }
        });

        panelHorarios.add(new JScrollPane(tblHorariosDisp), BorderLayout.CENTER);

        // Botones de estudiante
        JPanel barraEstudiante = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        btnInscribirme = new JButton("Inscribirme");
        btnVerMis      = new JButton("Ver mis inscripciones");
        btnRetirarme   = new JButton("Retirarme");
        barraEstudiante.add(btnInscribirme);
        barraEstudiante.add(btnVerMis);
        barraEstudiante.add(btnRetirarme);
        panelHorarios.add(barraEstudiante, BorderLayout.SOUTH);

        add(panelHorarios, BorderLayout.NORTH);

        //
        cargarHorariosDisponibles();


        String[] columnNames = {"ID", "Estudiante", "Curso - Horario", "Fecha Inscripción"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblInscripciones = new JTable(tableModel);
        tblInscripciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(tblInscripciones);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Todas las Inscripciones"));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        btnAdd = new JButton("Inscribir Estudiante");
        btnDelete = new JButton("Eliminar Inscripción");

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnDelete);

        add(buttonPanel, BorderLayout.SOUTH);

        boolean isAdmin = userRol.equals("admin");
        btnAdd.setVisible(isAdmin);    // Solo admin
        btnDelete.setVisible(isAdmin); // Solo admin

        JPanel panelMis = new JPanel(new BorderLayout(5,5));
        panelMis.setBorder(BorderFactory.createTitledBorder("Mis Inscripciones"));
        modeloMisInscripciones = new DefaultTableModel(new Object[]{"ID Inscripción","Curso - Horario","Fecha"}, 0){
            @Override public boolean isCellEditable(int r,int c){ return false; }
        };
        tblMisInscripciones = new JTable(modeloMisInscripciones);
        panelMis.add(new JScrollPane(tblMisInscripciones), BorderLayout.CENTER);
        panelMis.setPreferredSize(new Dimension(380, getHeight()));
        add(panelMis, BorderLayout.EAST);


        btnAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Estudiante> estudiantes = estudianteDAO.getAllEstudiantes();
                List<Horario> horarios = horarioDAO.getAllHorarios();

                if (estudiantes.isEmpty()) {
                    JOptionPane.showMessageDialog(InscripcionPanel.this, "No hay estudiantes registrados. Añada estudiantes primero.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (horarios.isEmpty()) {
                    JOptionPane.showMessageDialog(InscripcionPanel.this, "No hay horarios disponibles. Añada cursos y horarios primero.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                JComboBox<Estudiante> cbEstudiantes = new JComboBox<>(estudiantes.toArray(new Estudiante[0]));
                JComboBox<Horario> cbHorarios = new JComboBox<>(horarios.toArray(new Horario[0]));

                JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
                inputPanel.add(new JLabel("Estudiante:"));
                inputPanel.add(cbEstudiantes);
                inputPanel.add(new JLabel("Horario:"));
                inputPanel.add(cbHorarios);

                inputPanel.setPreferredSize(new Dimension(450, 150));

                int result = JOptionPane.showConfirmDialog(InscripcionPanel.this, inputPanel, "Inscribir Estudiante a Curso", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    try {
                        Estudiante selectedEstudiante = (Estudiante) cbEstudiantes.getSelectedItem();
                        Horario selectedHorario = (Horario) cbHorarios.getSelectedItem();

                        if (selectedEstudiante == null || selectedHorario == null) {
                            JOptionPane.showMessageDialog(InscripcionPanel.this, "Por favor, seleccione un estudiante y un horario.", "Error de Selección", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        Inscripcion nuevaInscripcion = new Inscripcion(
                                0,
                                selectedEstudiante.getId(),
                                selectedHorario.getId(),
                                new Date()
                        );

                        inscripcionDAO.addInscripcion(nuevaInscripcion);

                        JOptionPane.showMessageDialog(InscripcionPanel.this, "Inscripción realizada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        loadInscripciones();
                        if (estudianteActualId != null && estudianteActualId == selectedEstudiante.getId()) {
                            cargarMisInscripciones(selectedEstudiante.getId());
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(InscripcionPanel.this, "Error al inscribir: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        //  Eliminar Inscripción (Admin) ---
        btnDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = tblInscripciones.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(InscripcionPanel.this, "Por favor, seleccione una inscripción para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int confirm = JOptionPane.showConfirmDialog(InscripcionPanel.this, "¿Está seguro de que desea eliminar esta inscripción?", "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    int inscripcionId = (int) tableModel.getValueAt(selectedRow, 0);
                    try {
                        inscripcionDAO.deleteInscripcion(inscripcionId);
                        JOptionPane.showMessageDialog(InscripcionPanel.this, "Inscripción eliminada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        loadInscripciones();
                        if (estudianteActualId != null) {
                            cargarMisInscripciones(estudianteActualId);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(InscripcionPanel.this, "Error al eliminar inscripción: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });


        // Inscribirme
        btnInscribirme.addActionListener(e -> accionInscribirme());

        // Ver mis inscripciones
        btnVerMis.addActionListener(e -> {
            Integer estId = pedirEstudianteSiNecesario();
            if (estId == null) return;
            estudianteActualId = estId;
            cargarMisInscripciones(estId); // panel derecho

            ensureMisInsDialog();
            cargarMisInsDialog(estId);
            dlgMisInscripciones.setVisible(true);
        });

        // Retirarme
        btnRetirarme.addActionListener(e -> {
            if (estudianteActualId == null) {
                JOptionPane.showMessageDialog(this, "Primero usa 'Ver mis inscripciones' o inicia sesión.");
                return;
            }

            int filaPanel = tblMisInscripciones.getSelectedRow();
            if (filaPanel != -1) {
                int inscripcionId = Integer.parseInt(
                        modeloMisInscripciones.getValueAt(filaPanel, 0).toString()
                );
                int confirm = JOptionPane.showConfirmDialog(this, "¿Deseas retirarte de esta inscripción?", "Confirmar", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;

                try {
                    inscripcionDAO.deleteInscripcion(inscripcionId);
                    JOptionPane.showMessageDialog(this, "Inscripción eliminada.");
                    cargarMisInscripciones(estudianteActualId);
                    loadInscripciones();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "No se pudo eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
                return;
            }

            if (dlgMisInscripciones != null && dlgMisInscripciones.isVisible()) {
                int filaDlg = tblMisInsDialog.getSelectedRow();
                if (filaDlg != -1) {
                    int inscripcionId = Integer.parseInt(
                            modeloMisInsDialog.getValueAt(filaDlg, 0).toString()
                    );
                    int confirm = JOptionPane.showConfirmDialog(this, "¿Deseas retirarte de esta inscripción?", "Confirmar", JOptionPane.YES_NO_OPTION);
                    if (confirm != JOptionPane.YES_OPTION) return;

                    try {
                        inscripcionDAO.deleteInscripcion(inscripcionId);
                        JOptionPane.showMessageDialog(this, "Inscripción eliminada.");
                        cargarMisInscripciones(estudianteActualId);
                        cargarMisInsDialog(estudianteActualId);
                        loadInscripciones();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "No se pudo eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });


        btnInscribirme.setVisible(!isAdmin);
        btnVerMis.setVisible(!isAdmin);
        btnRetirarme.setVisible(!isAdmin);
    }


    private void loadInscripciones() {
        tableModel.setRowCount(0); // Clear the table
        List<Inscripcion> inscripciones = inscripcionDAO.getAllInscripciones();
        for (Inscripcion inscripcion : inscripciones) {
            tableModel.addRow(new Object[]{
                    inscripcion.getId(),
                    inscripcion.getEstudianteNombreCompleto(),
                    inscripcion.getCursoHorarioInfo(),
                    dateTimeFormat.format(inscripcion.getFechaInscripcion()) // Format date for display
            });
        }
    }


    /** (Opcional) Permite setear el estudiante actual desde fuera  */
    public void setEstudianteActualId(Integer estudianteId) {
        this.estudianteActualId = estudianteId;
        if (estudianteId != null) {
            cargarMisInscripciones(estudianteId);
        }
    }

    /** Carga horarios con nombre de curso visible. */
    private void cargarHorariosDisponibles() {
        cursosPorId.clear();
        List<Curso> cursos = cursoDAO.getAllCursos();
        for (Curso c : cursos) cursosPorId.put(c.getId(), c.getNombre());

        modeloHorariosDisp.setRowCount(0);
        List<Horario> horarios = horarioDAO.getAllHorarios();
        for (Horario h : horarios) {
            String nombreCurso = cursosPorId.getOrDefault(h.getIdCurso(), "Curso " + h.getIdCurso());
            modeloHorariosDisp.addRow(new Object[]{
                    h.getId(), nombreCurso, h.getDia(), h.getHoraInicio(), h.getHoraFin()
            });
        }
    }

    /** Carga “Mis Inscripciones”  para el estudiante indicado. */
    private void cargarMisInscripciones(int estudianteId) {
        modeloMisInscripciones.setRowCount(0);
        List<Inscripcion> todas = inscripcionDAO.getAllInscripciones();
        for (Inscripcion i : todas) {
            if (i.getEstudianteId() == estudianteId) {
                modeloMisInscripciones.addRow(new Object[]{
                        i.getId(), i.getCursoHorarioInfo(), dateTimeFormat.format(i.getFechaInscripcion())
                });
            }
        }
    }

    /** Pide seleccionar estudiante si no hay uno fijado. */
    private Integer pedirEstudianteSiNecesario() {
        if (estudianteActualId != null) return estudianteActualId;

        List<Estudiante> estudiantes = estudianteDAO.getAllEstudiantes();
        if (estudiantes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay estudiantes registrados.");
            return null;
        }
        JComboBox<Estudiante> cb = new JComboBox<>(estudiantes.toArray(new Estudiante[0]));
        int r = JOptionPane.showConfirmDialog(this, cb, "Selecciona tu usuario (estudiante)", JOptionPane.OK_CANCEL_OPTION);
        if (r == JOptionPane.OK_OPTION && cb.getSelectedItem() != null) {
            return ((Estudiante) cb.getSelectedItem()).getId();
        }
        return null;
    }

    /** Inscribirse en el horario seleccionado en la tabla superior. */
    private void accionInscribirme() {
        Integer estId = pedirEstudianteSiNecesario();
        if (estId == null) return;

        int filaView = tblHorariosDisp.getSelectedRow();
        if (filaView == -1) {
            JOptionPane.showMessageDialog(this, "Selecciona un horario disponible.");
            return;
        }
        int fila = tblHorariosDisp.convertRowIndexToModel(filaView);
        int horarioId = Integer.parseInt(modeloHorariosDisp.getValueAt(fila, 0).toString());

        try {
            Inscripcion nueva = new Inscripcion(0, estId, horarioId, new Date());
            inscripcionDAO.addInscripcion(nueva);
            JOptionPane.showMessageDialog(this, "¡Inscripción realizada!");
            cargarMisInscripciones(estId);
            loadInscripciones();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo inscribir: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ensureMisInsDialog() {
        if (dlgMisInscripciones != null) return;

        dlgMisInscripciones = new JDialog(SwingUtilities.getWindowAncestor(this), "Mis Inscripciones", Dialog.ModalityType.MODELESS);
        dlgMisInscripciones.setSize(600, 420);
        dlgMisInscripciones.setLocationRelativeTo(this);
        dlgMisInscripciones.setLayout(new BorderLayout(8,8));

        modeloMisInsDialog = new DefaultTableModel(new Object[]{"ID Inscripción", "Curso - Horario", "Fecha"}, 0) {
            @Override public boolean isCellEditable(int r,int c){ return false; }
        };
        tblMisInsDialog = new JTable(modeloMisInsDialog);
        dlgMisInscripciones.add(new JScrollPane(tblMisInsDialog), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        JButton btnRetirarmeDialog = new JButton("Retirarme");
        south.add(btnRetirarmeDialog);
        dlgMisInscripciones.add(south, BorderLayout.SOUTH);

        // Retirarme
        btnRetirarmeDialog.addActionListener(e -> {
            int fila = tblMisInsDialog.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(dlgMisInscripciones, "Selecciona una inscripción en la lista.");
                return;
            }
            int inscripcionId = Integer.parseInt(modeloMisInsDialog.getValueAt(fila, 0).toString());
            int confirm = JOptionPane.showConfirmDialog(dlgMisInscripciones, "¿Deseas retirarte de esta inscripción?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            try {
                inscripcionDAO.deleteInscripcion(inscripcionId);
                JOptionPane.showMessageDialog(dlgMisInscripciones, "Inscripción eliminada.");
                if (estudianteActualId != null) {
                    cargarMisInscripciones(estudianteActualId); // panel derecho
                    cargarMisInsDialog(estudianteActualId);     // diálogo
                }
                loadInscripciones(); // tabla central
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlgMisInscripciones, "No se pudo eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }


    private void cargarMisInsDialog(int estudianteId) {
        if (modeloMisInsDialog == null) return;
        modeloMisInsDialog.setRowCount(0);
        List<Inscripcion> todas = inscripcionDAO.getAllInscripciones();
        for (Inscripcion i : todas) {
            if (i.getEstudianteId() == estudianteId) {
                modeloMisInsDialog.addRow(new Object[]{
                        i.getId(),
                        i.getCursoHorarioInfo(),
                        dateTimeFormat.format(i.getFechaInscripcion())
                });
            }
        }
    }
}
