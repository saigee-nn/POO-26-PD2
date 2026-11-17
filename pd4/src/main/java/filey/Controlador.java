package filey;

import java.sql.SQLException;
import java.math.BigDecimal;
import java.time.LocalDate;

record Controlador(Consultas consultas, VistaConsola vista, Registros registros) {
    void listar() throws SQLException {
        vista.mostrar("Stands", consultas.stands());
        vista.mostrar("Editoriales", consultas.editoriales());
        vista.mostrar("Reservaciones", consultas.reservas());
    }

    void operar(String[] a) throws SQLException {
        if (a.length == 0) return;
        switch (a[0]) {
            case "editorial" -> {
                exigir(a, 3);
                registros.editorial(Integer.parseInt(a[1]), a[2]);
            }
            case "stand" -> {
                exigir(a, 5);
                registros.stand(Integer.parseInt(a[1]), new BigDecimal(a[2]), new BigDecimal(a[3]), new BigDecimal(a[4]));
            }
            case "reservar" -> {
                exigir(a, 6);
                registros.reservar(Integer.parseInt(a[1]), Integer.parseInt(a[2]), Integer.parseInt(a[3]),
                        LocalDate.parse(a[4]), LocalDate.parse(a[5]));
            }
            case "pagar" -> {
                exigir(a, 3);
                registros.pagar(Integer.parseInt(a[1]), new BigDecimal(a[2]));
            }
            case "prueba30" -> {
                exigir(a, 1);
                if (consultas.editoriales().stream().anyMatch(e -> e.id() == 3)) {
                    throw new IllegalArgumentException("El ID de editorial 3 ya existe. No se aplicó el ejemplo.");
                }
                int stand = consultas.stands().stream().mapToInt(Stand::id).max().orElse(0) + 1;
                int reserva = consultas.reservas().stream().mapToInt(Reserva::id).max().orElse(0) + 1;
                registros.editorial(3, "PruebaStandDB");
                registros.stand(stand, new BigDecimal("2"), new BigDecimal("5"), new BigDecimal("100"));
                LocalDate hoy = LocalDate.now();
                registros.reservar(reserva, stand, 3, hoy.plusDays(3), hoy.plusDays(10));
                registros.pagar(reserva, new BigDecimal("300.00"));
            }
            default -> throw new IllegalArgumentException("Operaciones: editorial, stand, reservar, pagar, prueba30.");
        }
    }

    private static void exigir(String[] args, int cantidad) {
        if (args.length != cantidad) throw new IllegalArgumentException("Revisa los argumentos en el README.");
    }
}
