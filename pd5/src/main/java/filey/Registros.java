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

    void editarEditorial(int id, String nombre) throws SQLException {
        var e = new filey.datos.Editorial(id, nombre);
        cambiar("UPDATE pd4.editorial SET nombre=? WHERE id=?", e.getNombre(), id);
    }

    void editarStand(int id, BigDecimal ancho, BigDecimal largo, BigDecimal tarifa) throws SQLException {
        var s = new filey.datos.Stand(id, ancho, largo, tarifa);
        cargarStand(id, "DISPONIBLE");
        sinReferencias("SELECT 1 FROM pd4.reserva WHERE stand_id=?", id, "El stand tiene reservas.");
        cambiar("UPDATE pd4.stand SET ancho=?,largo=?,precio_por_metro=? WHERE id=?",
                ancho, largo, s.getPrecioPorMetroCuadrado(), id);
    }

    void editarReserva(int id, LocalDate vence, LocalDate limite) throws SQLException {
        try (var q = db.prepareStatement("SELECT fecha FROM pd4.reserva WHERE id=? FOR UPDATE")) {
            q.setInt(1, id);
            try (var r = q.executeQuery()) {
                if (!r.next()) throw new IllegalArgumentException("Reserva inexistente.");
                if (vence.isBefore(r.getObject(1, LocalDate.class)) || vence.isAfter(limite))
                    throw new IllegalArgumentException("Las fechas de la reserva no son válidas.");
            }
        }
        sinReferencias("SELECT 1 FROM pd4.pago WHERE reserva_id=?", id, "No se modifican plazos de reservas con pagos.");
        cambiar("UPDATE pd4.reserva SET vencimiento=?,limite_liquidacion=? WHERE id=?", vence, limite, id);
    }

    void eliminarEditorial(int id) throws SQLException {
        // Las claves foráneas impiden borrar editoriales con reservas.
        cambiar("DELETE FROM pd4.editorial WHERE id=?", id);
    }

    void eliminarStand(int id) throws SQLException {
        cargarStand(id, "DISPONIBLE");
        cambiar("DELETE FROM pd4.stand WHERE id=?", id);
    }

    void eliminarReserva(int id) throws SQLException {
        int standId;
        try (var q = db.prepareStatement("SELECT stand_id FROM pd4.reserva WHERE id=? FOR UPDATE")) {
            q.setInt(1, id);
            try (var r = q.executeQuery()) {
                if (!r.next()) throw new IllegalArgumentException("Reserva inexistente.");
                standId = r.getInt(1);
            }
        }
        cargarStand(standId, "RESERVADO");
        sinReferencias("SELECT 1 FROM pd4.pago WHERE reserva_id=?", id, "No se eliminan reservas con pagos.");
        sinReferencias("SELECT 1 FROM pd4.reserva WHERE stand_id=" + standId + " AND id<>?", id,
                "El stand tiene otras reservas; no se puede liberar automáticamente.");
        cambiar("DELETE FROM pd4.reserva WHERE id=?", id);
        cambiar("UPDATE pd4.stand SET estado='DISPONIBLE' WHERE id=?", standId);
    }

    private void sinReferencias(String sql, int id, String mensaje) throws SQLException {
        try (var q = db.prepareStatement(sql)) {
            q.setInt(1, id);
            try (var r = q.executeQuery()) {
                if (r.next()) throw new IllegalStateException(mensaje);
            }
        }
    }

    private void cambiar(String sql, Object... valores) throws SQLException {
        try (var q = db.prepareStatement(sql)) {
            for (int i = 0; i < valores.length; i++) q.setObject(i + 1, valores[i]);
            if (q.executeUpdate() == 0) throw new IllegalArgumentException("Registro inexistente.");
        }
    }

    private void ejecutar(String sql, Object... valores) throws SQLException {
        try (var consulta = db.prepareStatement(sql)) {
            for (int i = 0; i < valores.length; i++) consulta.setObject(i + 1, valores[i]);
            consulta.executeUpdate();
        }
    }
}
