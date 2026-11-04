package filey;

import filey.datos.Configuracion;
import filey.datos.Editorial;
import filey.datos.Reserva;
import filey.datos.Stand;
import filey.negocio.SistemaFiley;
import filey.notificaciones.Notificador;
import java.math.BigDecimal;
import java.time.LocalDate;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        // Fechas de ejemplo relativas a hoy; se pueden cambiar sin modificar los servicios.
        LocalDate hoy = LocalDate.now();
        Configuracion configuracion = new Configuracion(hoy.plusDays(4), hoy.plusDays(10),
                hoy.plusDays(6), 3, 2);
        SistemaFiley sistema = new SistemaFiley(configuracion, new Notificador());
        Stand primero = new Stand(1, new BigDecimal("2"), new BigDecimal("5"), new BigDecimal("100"));
        Stand segundo = new Stand(2, new BigDecimal("3"), new BigDecimal("4"), new BigDecimal("100"));
        sistema.registrarStand(primero);
        sistema.registrarStand(segundo);
        sistema.registrarEditorial(new Editorial(1, "Lecturas del Mayab"));
        sistema.registrarEditorial(new Editorial(2, "Letras del Sur"));
        System.out.println("Stands disponibles: " + sistema.consultarDisponibles().stream().map(Stand::getId).toList());
        Reserva reserva = sistema.crearReserva(1, 1, hoy);
        System.out.println("Reserva " + reserva.getId() + " de " + reserva.getEditorial().getNombre()
                + "; total: $" + reserva.getPrecioTotal() + "; vence: " + reserva.getVencimiento());
        sistema.registrarPago(reserva.getId(), new BigDecimal("100"), hoy);
        mostrar(reserva);
        System.out.println("--- Comprobación de avisos antes del segundo pago ---");
        sistema.comprobarFechas(hoy.plusDays(2));
        sistema.registrarPago(reserva.getId(), new BigDecimal("50"), hoy.plusDays(2));
        mostrar(reserva);
        sistema.bloquearStand(2);
        System.out.println("Stand 2: " + segundo.getEstado());
        sistema.desbloquearStand(2);
        System.out.println("Stand 2: " + segundo.getEstado());
        Reserva sinPago = sistema.crearReserva(2, 2, hoy.plusDays(2));
        sistema.comprobarFechas(hoy.plusDays(4));
        sistema.comprobarFechas(hoy.plusDays(6));
        System.out.println("Reserva " + sinPago.getId() + " vencida: " + sinPago.isVencida()
                + "; stand 2: " + segundo.getEstado());
        System.out.println("--- Aviso de liquidación; la segunda consulta no duplica avisos ---");
        sistema.comprobarFechas(hoy.plusDays(8));
        sistema.comprobarFechas(hoy.plusDays(8));
        sistema.registrarPago(reserva.getId(), new BigDecimal("850"), hoy.plusDays(10));
        mostrar(reserva);
    }

    private static void mostrar(Reserva reserva) {
        System.out.println("Stand " + reserva.getStand().getId() + ": " + reserva.getStand().getEstado()
                + "; abonado: $" + reserva.getAbonado() + "; pendiente: $" + reserva.getPendiente()
                + "; porcentaje: " + reserva.getPorcentajePagado() + "%");
    }
}
