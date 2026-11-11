package filey.negocio;

import filey.datos.Configuracion;
import filey.datos.Editorial;
import filey.datos.EstadoStand;
import filey.datos.Reserva;
import filey.datos.Stand;
import filey.notificaciones.Sujeto;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class SistemaFiley {
    private final Configuracion configuracion;
    private final Sujeto notificador;
    private final Map<Integer, Stand> stands = new LinkedHashMap<>();
    private final Map<Integer, Editorial> editoriales = new LinkedHashMap<>();
    private final Map<Integer, Reserva> reservas = new LinkedHashMap<>();
    private final Set<String> avisosEmitidos = new HashSet<>();
    private int siguienteReserva = 1;

    public SistemaFiley(Configuracion configuracion, Sujeto notificador) {
        this.configuracion = Objects.requireNonNull(configuracion);
        this.notificador = Objects.requireNonNull(notificador);
    }

    public void registrarStand(Stand stand) {
        Objects.requireNonNull(stand);
        if (stand.getEstado() != EstadoStand.DISPONIBLE) {
            throw new IllegalArgumentException("El stand debe registrarse disponible.");
        }
        if (stands.putIfAbsent(stand.getId(), stand) != null) {
            throw new IllegalArgumentException("ID de stand duplicado.");
        }
    }

    public void registrarEditorial(Editorial editorial) {
        Objects.requireNonNull(editorial);
        if (editoriales.putIfAbsent(editorial.getId(), editorial) != null) {
            throw new IllegalArgumentException("ID de editorial duplicado.");
        }
        notificador.suscribir(editorial.getId(), editorial);
    }

    public List<Stand> consultarDisponibles() {
        return stands.values().stream().filter(s -> s.getEstado() == EstadoStand.DISPONIBLE).toList();
    }
    public List<Reserva> consultarReservas() { return List.copyOf(reservas.values()); }

    public Reserva crearReserva(int idEditorial, int idStand, LocalDate fecha) {
        Objects.requireNonNull(fecha);
        Editorial editorial = editoriales.get(idEditorial);
        if (editorial == null) { throw new IllegalArgumentException("Editorial inexistente."); }
        Stand stand = buscarStand(idStand);
        if (fecha.isAfter(configuracion.getLimiteReserva())) {
            throw new IllegalStateException("Se ha superado la fecha límite de reserva.");
        }
        LocalDate vencimiento = fecha.plusDays(configuracion.getDiasParaPagarReserva());
        if (vencimiento.isAfter(configuracion.getLimiteReserva())) {
            vencimiento = configuracion.getLimiteReserva();
        }
        Reserva reserva = stand.reservar(siguienteReserva, editorial, fecha, vencimiento,
                configuracion.getLimiteLiquidacion());
        reservas.put(siguienteReserva++, reserva);
        return reserva;
    }

    public void registrarPago(int idReserva, BigDecimal importe, LocalDate fecha) {
        buscarReserva(idReserva).registrarPago(importe, fecha);
    }
    public void bloquearStand(int idStand) { buscarStand(idStand).bloquear(); }
    public void desbloquearStand(int idStand) { buscarStand(idStand).desbloquear(); }

    /** Debe invocarse con la fecha de consulta; no requiere tareas en segundo plano. */
    public void comprobarFechas(LocalDate fecha) {
        Objects.requireNonNull(fecha);
        for (Reserva reserva : reservas.values()) {
            if (fecha.isBefore(reserva.getFechaCreacion())) { continue; }
            reserva.vencerSiCorresponde(fecha);
            if (reserva.isVencida() || reserva.getPendiente().signum() == 0) { continue; }
            if (!reserva.alcanzaMinimo()) {
                avisar(reserva, "RESERVA", reserva.getVencimiento(), fecha,
                        "Vence el plazo para cubrir al menos el 15% de la reserva");
            }
            avisar(reserva, "DESCUENTO", configuracion.getLimiteDescuento(), fecha,
                    "Vence el plazo de descuento por pronto pago");
            avisar(reserva, "LIQUIDACION", reserva.getLimiteLiquidacion(), fecha,
                    "Vence el plazo para liquidar el saldo");
        }
    }

    private void avisar(Reserva reserva, String tipo, LocalDate limite, LocalDate fecha, String texto) {
        if (fecha.isBefore(limite.minusDays(configuracion.getDiasAnticipacionAviso()))
                || fecha.isAfter(limite)) { return; }
        String clave = reserva.getId() + ":" + tipo + ":" + limite;
        if (!avisosEmitidos.contains(clave)) {
            notificador.notificar(reserva.getEditorial().getId(), texto + " el " + limite
                    + " (reserva " + reserva.getId() + ", stand " + reserva.getStand().getId() + ").");
            avisosEmitidos.add(clave);
        }
    }

    private Stand buscarStand(int id) {
        Stand stand = stands.get(id);
        if (stand == null) { throw new IllegalArgumentException("Stand inexistente."); }
        return stand;
    }
    private Reserva buscarReserva(int id) {
        Reserva reserva = reservas.get(id);
        if (reserva == null) { throw new IllegalArgumentException("Reserva inexistente."); }
        return reserva;
    }
}
