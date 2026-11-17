package filey;

import java.sql.*;

public class Main {
    public static void main(String[] args) {
        try (Connection db = ConexionBD.abrir()) {
            System.out.println("Conexión establecida con " + db.getCatalog());
            db.setAutoCommit(false);
            try {
                try (var sql = db.createStatement()) {
                    sql.execute("SELECT pg_advisory_xact_lock(4102604)");
                    boolean nueva;
                    try (var fila = sql.executeQuery("SELECT to_regnamespace('pd4') IS NULL")) {
                        fila.next();
                        nueva = fila.getBoolean(1);
                    }
                    sql.execute(Inicializacion.leer("filey.sql"));
                    if (nueva) sql.execute(Inicializacion.leer("ejemplo.sql"));
                    sql.execute(Inicializacion.leer("historial_pd2.sql"));
                }
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
