package filey;

import filey.datos.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;

final class Registros {
    private final Connection db;

    Registros(Connection db) {
        this.db = db;
    }

    void editorial(int id, String nombre) throws SQLException {
        var editorial = new filey.datos.Editorial(id, nombre);
        ejecutar("INSERT INTO pd4.editorial(id,nombre) VALUES (?,?)", editorial.getId(), editorial.getNombre());
    }

    void stand(int id, BigDecimal ancho, BigDecimal largo, BigDecimal tarifa) throws SQLException {
        var stand = new filey.datos.Stand(id, ancho, largo, tarifa);
        ejecutar("INSERT INTO pd4.stand(id,ancho,largo,precio_por_metro,estado) VALUES (?,?,?,?,?)",
                id, ancho, largo, stand.getPrecioPorMetroCuadrado(), stand.getEstado().name());
    }

    void reservar(int id, int standId, int editorialId, LocalDate vence, LocalDate limite) throws SQLException {
        var stand = cargarStand(standId, "DISPONIBLE");
        var editorial = cargarEditorial(editorialId);
        LocalDate hoy = LocalDate.now();
        stand.reservar(id, editorial, hoy, vence, limite);
        ejecutar("INSERT INTO pd4.reserva(id,stand_id,editorial_id,fecha,vencimiento,limite_liquidacion) VALUES (?,?,?,?,?,?)",
                id, standId, editorialId, hoy, vence, limite);
        estado(stand);
    }

    void pagar(int reservaId, BigDecimal importe) throws SQLException {
        String sql = "SELECT stand_id, editorial_id, fecha, vencimiento, limite_liquidacion FROM pd4.reserva WHERE id=? FOR UPDATE";
        try (var consulta = db.prepareStatement(sql)) {
            consulta.setInt(1, reservaId);
            try (var fila = consulta.executeQuery()) {
                if (!fila.next()) throw new IllegalArgumentException("Reserva inexistente.");
                var vence = fila.getObject(4, LocalDate.class);
                var limite = fila.getObject(5, LocalDate.class);
                if (vence == null || limite == null)
                    throw new IllegalArgumentException("Esta reserva anterior no tiene plazos. Usa una reserva nueva para la prueba de pagos.");
                var stand = cargarStand(fila.getInt(1), "RESERVADO", "OCUPADO");
                var reserva = stand.reservar(reservaId, cargarEditorial(fila.getInt(2)),
                        fila.getObject(3, LocalDate.class), vence, limite);
                cargarPagos(reserva);
                LocalDate hoy = LocalDate.now();
                reserva.registrarPago(importe, hoy);
                ejecutar("INSERT INTO pd4.pago(reserva_id,importe,fecha) VALUES (?,?,?)", reservaId, importe, hoy);
                estado(stand);
            }
        }
    }

    private void cargarPagos(filey.datos.Reserva reserva) throws SQLException {
        try (var consulta = db.prepareStatement("SELECT importe,fecha FROM pd4.pago WHERE reserva_id=? ORDER BY id")) {
            consulta.setInt(1, reserva.getId());
            try (var filas = consulta.executeQuery()) {
                while (filas.next()) reserva.registrarPago(filas.getBigDecimal(1), filas.getObject(2, LocalDate.class));
            }
        }
    }

    private filey.datos.Editorial cargarEditorial(int id) throws SQLException {
        try (var consulta = db.prepareStatement("SELECT nombre FROM pd4.editorial WHERE id=?")) {
            consulta.setInt(1, id);
            try (var fila = consulta.executeQuery()) {
                if (!fila.next()) throw new IllegalArgumentException("Editorial inexistente.");
                return new filey.datos.Editorial(id, fila.getString(1));
            }
        }
    }

    private filey.datos.Stand cargarStand(int id, String... estados) throws SQLException {
        try (var consulta = db.prepareStatement("SELECT ancho,largo,precio_por_metro,estado FROM pd4.stand WHERE id=? FOR UPDATE")) {
            consulta.setInt(1, id);
            try (var fila = consulta.executeQuery()) {
                if (!fila.next()) throw new IllegalArgumentException("Stand inexistente.");
                if (!java.util.Arrays.asList(estados).contains(fila.getString(4)))
                    throw new IllegalArgumentException("El estado del stand impide la operación.");
                return new filey.datos.Stand(id, fila.getBigDecimal(1), fila.getBigDecimal(2), fila.getBigDecimal(3));
            }
        }
    }

    private void estado(filey.datos.Stand stand) throws SQLException {
        ejecutar("UPDATE pd4.stand SET estado=? WHERE id=?", stand.getEstado().name(), stand.getId());
    }

    private void ejecutar(String sql, Object... valores) throws SQLException {
        try (var consulta = db.prepareStatement(sql)) {
            for (int i = 0; i < valores.length; i++) consulta.setObject(i + 1, valores[i]);
            consulta.executeUpdate();
        }
    }
}
