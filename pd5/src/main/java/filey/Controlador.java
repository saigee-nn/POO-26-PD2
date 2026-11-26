package filey;

import java.sql.SQLException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

record Controlador(Consultas consultas, VistaConsola vista, Registros registros) {
    void listar() throws SQLException {
        vista.mostrar("Stands", consultas.stands());
        vista.mostrar("Editoriales", consultas.editoriales());
        vista.mostrar("Reservaciones", consultas.reservas());
    }

    void operar(String[] a) throws SQLException {
        if (a.length == 0) return;
        switch (a[0]) {
            case "editar-editorial" -> {
                exigir(a, 3);
                registros.editarEditorial(Integer.parseInt(a[1]), a[2]);
            }
            case "editar-stand" -> {
                exigir(a, 5);
                registros.editarStand(Integer.parseInt(a[1]), new BigDecimal(a[2]), new BigDecimal(a[3]), new BigDecimal(a[4]));
            }
            case "editar-reserva" -> {
                exigir(a, 4);
                registros.editarReserva(Integer.parseInt(a[1]), LocalDate.parse(a[2]), LocalDate.parse(a[3]));
            }
            case "eliminar-editorial" -> { exigir(a, 2); registros.eliminarEditorial(Integer.parseInt(a[1])); }
            case "eliminar-stand" -> { exigir(a, 2); registros.eliminarStand(Integer.parseInt(a[1])); }
            case "eliminar-reserva" -> { exigir(a, 2); registros.eliminarReserva(Integer.parseInt(a[1])); }
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
                pruebaConDosStands();
            }
            default -> throw new IllegalArgumentException("Operación desconocida. Consulta los comandos del README.");
        }
    }

    private void pruebaConDosStands() throws SQLException {
        final int editorialId = 3;
        final int reservaPrueba = 30003;
        if (consultas.reservas().stream().anyMatch(r -> r.id() == reservaPrueba)) {
            throw new IllegalArgumentException("La reserva 30003 ya existe. No se repitió la prueba.");
        }
        var editorial = consultas.editoriales().stream().filter(e -> e.id() == editorialId).findFirst();
        if (editorial.isEmpty()) {
            registros.editorial(editorialId, "PruebaStandDB");
        } else if (!editorial.get().nombre().equals("PruebaStandDB")) {
            throw new IllegalArgumentException("El ID 3 pertenece a otra editorial.");
        }

        int primerId = consultas.stands().stream().mapToInt(Stand::id).max().orElse(0) + 1;
        var azar = ThreadLocalRandom.current();
        int elegido = primerId + azar.nextInt(2);
        BigDecimal tarifa = new BigDecimal("100.00");
        BigDecimal total = BigDecimal.ZERO;
        for (int id = primerId; id <= primerId + 1; id++) {
            BigDecimal ancho = BigDecimal.valueOf(azar.nextInt(2, 6));
            BigDecimal largo = BigDecimal.valueOf(azar.nextInt(3, 8));
            registros.stand(id, ancho, largo, tarifa);
            if (id == elegido) total = ancho.multiply(largo).multiply(tarifa);
        }
        LocalDate hoy = LocalDate.now();
        registros.reservar(reservaPrueba, elegido, editorialId, hoy.plusDays(3), hoy.plusDays(10));
        BigDecimal abono = total.multiply(new BigDecimal("0.30")).setScale(2);
        registros.pagar(reservaPrueba, abono);
        System.out.printf("Reserva %d, stand %d, total $%s, abono del 30%% $%s%n",
                reservaPrueba, elegido, total, abono);
    }

    private static void exigir(String[] args, int cantidad) {
        if (args.length != cantidad) throw new IllegalArgumentException("Revisa los argumentos en el README.");
    }
}
