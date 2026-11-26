package filey;

import java.sql.*;
import java.util.*;

final class Consultas {
    private final Connection conexion;

    Consultas(Connection conexion) {
        this.conexion = conexion;
    }

    List<Stand> stands() throws SQLException {
        List<Stand> lista = new ArrayList<>();
        String sql = "SELECT id, ancho, largo, precio_por_metro, estado FROM pd4.stand ORDER BY id";
        try (PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet r = consulta.executeQuery()) {
            while (r.next()) {
                lista.add(new Stand(r.getInt(1), r.getBigDecimal(2), r.getBigDecimal(3), r.getBigDecimal(4), r.getString(5)));
            }
        }
        return lista;
    }

    List<Editorial> editoriales() throws SQLException {
        List<Editorial> lista = new ArrayList<>();
        String sql = "SELECT id, nombre FROM pd4.editorial ORDER BY id";
        try (PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet r = consulta.executeQuery()) {
            while (r.next()) {
                lista.add(new Editorial(r.getInt(1), r.getString(2)));
            }
        }
        return lista;
    }

    List<Reserva> reservas() throws SQLException {
        List<Reserva> lista = new ArrayList<>();
        String sql = "SELECT id, stand_id, editorial_id, fecha FROM pd4.reserva ORDER BY id";
        try (PreparedStatement consulta = conexion.prepareStatement(sql);
             ResultSet r = consulta.executeQuery()) {
            while (r.next()) {
                lista.add(new Reserva(r.getInt(1), r.getInt(2), r.getInt(3), r.getDate(4).toLocalDate()));
            }
        }
        return lista;
    }
}
