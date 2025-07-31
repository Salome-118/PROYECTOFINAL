package VIEW;

import DaoCV.CursoDAO;
import DaoCV.HorarioDAO;
import ModelCV.Curso;
import ModelCV.Horario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class HorarioPanel extends JPanel {

    private JTable tablaHorarios;
    private DefaultTableModel modeloTabla;

    // REEMPLAZO: antes JTextField txtCursoId
    private JComboBox<CursoItem> cboCurso;
    private JTextField txtDia, txtHoraInicio, txtHoraFin, txtId;

    private final HorarioDAO horarioDAO = new HorarioDAO();
    private final CursoDAO cursoDAO = new CursoDAO();

    public HorarioPanel() {
        setLayout(new BorderLayout(10,10));

        // ===== Tabla =====
        modeloTabla = new DefaultTableModel(new Object[]{"ID","Curso ID","Día","Hora Inicio","Hora Fin"},0){
            @Override public boolean isCellEditable(int r,int c){ return false; }
        };
        tablaHorarios = new JTable(modeloTabla);
        tablaHorarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tablaHorarios), BorderLayout.CENTER);

        // ===== Formulario =====
        JPanel form = new JPanel(new GridLayout(5,2,8,8));
        form.setBorder(BorderFactory.createTitledBorder("Datos del horario"));

        form.add(new JLabel("ID (solo lectura):"));
        txtId = new JTextField(); txtId.setEditable(false);
        form.add(txtId);

        form.add(new JLabel("Curso:"));
        cboCurso = new JComboBox<>();
        form.add(cboCurso);

        form.add(new JLabel("Día (ej. Lunes):"));
        txtDia = new JTextField();
        form.add(txtDia);

        form.add(new JLabel("Hora Inicio (HH:mm):"));
        txtHoraInicio = new JTextField();
        form.add(txtHoraInicio);

        form.add(new JLabel("Hora Fin (HH:mm):"));
        txtHoraFin = new JTextField();
        form.add(txtHoraFin);

        add(form, BorderLayout.NORTH);

        // ===== Botones =====
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAgregar = new JButton("Agregar");
        JButton btnEditar  = new JButton("Editar");
        JButton btnEliminar= new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        acciones.add(btnAgregar); acciones.add(btnEditar);
        acciones.add(btnEliminar); acciones.add(btnLimpiar);
        add(acciones, BorderLayout.SOUTH);

        // Eventos
        btnAgregar.addActionListener(e -> onAgregar());
        btnEditar.addActionListener(e -> onEditar());
        btnEliminar.addActionListener(e -> onEliminar());
        btnLimpiar.addActionListener(e -> limpiar());

        tablaHorarios.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                int f = tablaHorarios.getSelectedRow();
                if (f>=0){
                    txtId.setText(String.valueOf(modeloTabla.getValueAt(f,0)));
                    int cursoId = Integer.parseInt(modeloTabla.getValueAt(f,1).toString());
                    seleccionarCursoEnCombo(cursoId);
                    txtDia.setText(String.valueOf(modeloTabla.getValueAt(f,2)));
                    txtHoraInicio.setText(String.valueOf(modeloTabla.getValueAt(f,3)));
                    txtHoraFin.setText(String.valueOf(modeloTabla.getValueAt(f,4)));
                }
            }
        });

        // Carga inicial
        cargarCursosEnCombo();
        cargarHorarios();
    }

    private void cargarCursosEnCombo(){
        cboCurso.removeAllItems();
        List<Curso> cursos = cursoDAO.getAllCursos();
        for (Curso c: cursos){
            cboCurso.addItem(new CursoItem(c.getId(), c.getNombre()));
        }
    }

    private void seleccionarCursoEnCombo(int cursoId){
        for (int i=0;i<cboCurso.getItemCount();i++){
            if (cboCurso.getItemAt(i).id == cursoId){
                cboCurso.setSelectedIndex(i); return;
            }
        }
        // si no lo encuentra, lo deja sin selección
        if (cboCurso.getItemCount()>0) cboCurso.setSelectedIndex(0);
    }

    private void cargarHorarios() {
        modeloTabla.setRowCount(0);
        List<Horario> lista = horarioDAO.getAllHorarios();
        for (Horario h : lista) {
            modeloTabla.addRow(new Object[]{h.getId(), h.getIdCurso(), h.getDia(), h.getHoraInicio(), h.getHoraFin()});
        }
    }

    private void onAgregar(){
        CursoItem sel = (CursoItem) cboCurso.getSelectedItem();
        if (sel == null){ JOptionPane.showMessageDialog(this,"Selecciona un curso."); return; }
        String dia = txtDia.getText().trim();
        String ini = txtHoraInicio.getText().trim();
        String fin = txtHoraFin.getText().trim();
        if (dia.isEmpty() || ini.isEmpty() || fin.isEmpty()){
            JOptionPane.showMessageDialog(this,"Completa todos los campos."); return;
        }
        if (!esHoraValida(ini) || !esHoraValida(fin)){
            JOptionPane.showMessageDialog(this,"Formato de hora inválido. Usa HH:mm."); return;
        }

        Horario h = new Horario(0, sel.id, dia, ini, fin);
        boolean ok = horarioDAO.insertHorario(h);
        if (ok){
            cargarHorarios(); limpiar();
            JOptionPane.showMessageDialog(this,"Horario agregado.");
        }else{
            JOptionPane.showMessageDialog(this,"No se pudo agregar.\n" + horarioDAO.getLastError(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEditar(){
        int f = tablaHorarios.getSelectedRow();
        if (f<0){ JOptionPane.showMessageDialog(this,"Selecciona un horario."); return; }
        CursoItem sel = (CursoItem) cboCurso.getSelectedItem();
        if (sel == null){ JOptionPane.showMessageDialog(this,"Selecciona un curso."); return; }

        String idStr = txtId.getText().trim();
        String dia = txtDia.getText().trim();
        String ini = txtHoraInicio.getText().trim();
        String fin = txtHoraFin.getText().trim();
        if (idStr.isEmpty() || dia.isEmpty() || ini.isEmpty() || fin.isEmpty()){
            JOptionPane.showMessageDialog(this,"Completa todos los campos."); return;
        }
        if (!esHoraValida(ini) || !esHoraValida(fin)){
            JOptionPane.showMessageDialog(this,"Formato de hora inválido. Usa HH:mm."); return;
        }

        int id = Integer.parseInt(idStr);
        Horario h = new Horario(id, sel.id, dia, ini, fin);
        boolean ok = horarioDAO.updateHorario(h);
        if (ok){
            cargarHorarios(); limpiar();
            JOptionPane.showMessageDialog(this,"Horario actualizado.");
        }else{
            JOptionPane.showMessageDialog(this,"No se pudo actualizar.\n" + horarioDAO.getLastError(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onEliminar(){
        int f = tablaHorarios.getSelectedRow();
        if (f<0){ JOptionPane.showMessageDialog(this,"Selecciona un horario."); return; }
        int id = Integer.parseInt(modeloTabla.getValueAt(f,0).toString());
        if (JOptionPane.showConfirmDialog(this,"¿Eliminar horario id="+id+"?","Confirmar",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        boolean ok = horarioDAO.deleteHorario(id);
        if (ok){
            cargarHorarios(); limpiar();
            JOptionPane.showMessageDialog(this,"Horario eliminado.");
        }else{
            JOptionPane.showMessageDialog(this,"No se pudo eliminar.\n" + horarioDAO.getLastError(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiar(){
        txtId.setText("");
        txtDia.setText("");
        txtHoraInicio.setText("");
        txtHoraFin.setText("");
        tablaHorarios.clearSelection();
        if (cboCurso.getItemCount()>0) cboCurso.setSelectedIndex(0);
    }

    private boolean esHoraValida(String hhmm){
        return hhmm.matches("^([01]?\\d|2[0-3]):[0-5]\\d$");
    }

    // Item de combo para cursos
    private static class CursoItem {
        final int id; final String nombre;
        CursoItem(int id, String nombre){ this.id=id; this.nombre=nombre; }
        @Override public String toString(){ return id + " - " + nombre; }
    }
}
