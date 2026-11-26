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
                try { db.rollback(); } catch (SQLException fallo) { e.addSuppressed(fallo); }
                throw e;
            }
        } catch (SQLException e) {
            String mensaje = switch (e.getSQLState() == null ? "" : e.getSQLState()) {
                case "23505" -> "El identificador ya existe.";
                case "23503" -> "La operación afecta registros relacionados o referencia uno inexistente.";
                case "23514" -> "Los valores no cumplen las restricciones de la tabla.";
                default -> "No se pudo completar la operación en la BD: " + e.getMessage();
            };
            System.err.println(mensaje);
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

}
