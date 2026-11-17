package filey;

import java.io.IOException;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.SQLException;

final class Inicializacion {
    // Se ejecuta dentro de la transacción abierta por Main.
    static void preparar(Connection conexion) throws SQLException, IOException {
        try (var sql = conexion.createStatement()) {
            sql.execute("SELECT pg_advisory_xact_lock(4102604)");
            boolean nueva;
            try (var fila = sql.executeQuery("SELECT to_regnamespace('pd4') IS NULL")) {
                fila.next();
                nueva = fila.getBoolean(1);
            }
            sql.execute(leer("filey.sql"));
            if (nueva) sql.execute(leer("ejemplo.sql"));
            sql.execute(leer("historial_pd2.sql"));
        }
    }

    private static String leer(String archivo) throws IOException {
        return Files.readString(Path.of("database", archivo));
    }
}
