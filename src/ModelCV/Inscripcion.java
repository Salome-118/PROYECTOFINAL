package ModelCV;

import java.util.Date;
public class Inscripcion {
    private int id;
    private int estudianteId;
    private int horarioId;
    private Date fechaInscripcion;

    // Optional: To store student and course/schedule names for display purposes
    private String estudianteNombreCompleto;
    private String cursoHorarioInfo; // e.g., "Programación - Lunes 09:00-10:30"

    /**
     * Constructor for Inscripcion.
     * @param id The unique ID of the enrollment.
     * @param estudianteId The ID of the enrolled student.
     * @param horarioId The ID of the schedule the student is enrolled in.
     * @param fechaInscripcion The date and time of the enrollment.
     */
    public Inscripcion(int id, int estudianteId, int horarioId, Date fechaInscripcion) {
        this.id = id;
        this.estudianteId = estudianteId;
        this.horarioId = horarioId;
        this.fechaInscripcion = fechaInscripcion;
    }

    //Constructor for Inscripcion including display names.

    public Inscripcion(int id, int estudianteId, int horarioId, Date fechaInscripcion, String estudianteNombreCompleto, String cursoHorarioInfo) {
        this(id, estudianteId, horarioId, fechaInscripcion);
        this.estudianteNombreCompleto = estudianteNombreCompleto;
        this.cursoHorarioInfo = cursoHorarioInfo;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getEstudianteId() {
        return estudianteId;
    }

    public int getHorarioId() {
        return horarioId;
    }

    public Date getFechaInscripcion() {
        return fechaInscripcion;
    }

    public String getEstudianteNombreCompleto() {
        return estudianteNombreCompleto;
    }

    public String getCursoHorarioInfo() {
        return cursoHorarioInfo;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setEstudianteId(int estudianteId) {
        this.estudianteId = estudianteId;
    }

    public void setHorarioId(int horarioId) {
        this.horarioId = horarioId;
    }

    public void setFechaInscripcion(Date fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public void setEstudianteNombreCompleto(String estudianteNombreCompleto) {
        this.estudianteNombreCompleto = estudianteNombreCompleto;
    }

    public void setCursoHorarioInfo(String cursoHorarioInfo) {
        this.cursoHorarioInfo = cursoHorarioInfo;
    }
}
