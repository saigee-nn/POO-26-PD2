package filey;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        try (Connection db = ConexionBD.abrir()) {
            System.out.println("Conexión establecida con " + db.getCatalog());
            db.setAutoCommit(false);
            try {
                Inicializacion.preparar(db);
                var controlador = new Controlador(new Consultas(db), new VistaConsola(), new Registros(db));
                controlador.operar(args);
                db.commit();
                controlador.listar();
            } catch (Exception e) {
                db.rollback();
                throw e;
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

}
