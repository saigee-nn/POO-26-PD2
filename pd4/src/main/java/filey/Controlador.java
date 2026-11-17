package filey;

import java.sql.SQLException;

record Controlador(Consultas consultas, VistaConsola vista) {
    void listar() throws SQLException {
        vista.mostrar("Stands", consultas.stands());
        vista.mostrar("Editoriales", consultas.editoriales());
        vista.mostrar("Reservaciones", consultas.reservas());
    }
}
