package filey.datos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

public final class Stand {
    private final int id;
    private final BigDecimal ancho;
    private final BigDecimal largo;
    private final BigDecimal precioPorMetroCuadrado;
    private EstadoStand estado = EstadoStand.DISPONIBLE;
    private Reserva reservaActual;

    public Stand(int id, BigDecimal ancho, BigDecimal largo, BigDecimal precioPorMetroCuadrado) {
        if (id <= 0 || Objects.requireNonNull(ancho).signum() <= 0
                || Objects.requireNonNull(largo).signum() <= 0
                || Objects.requireNonNull(precioPorMetroCuadrado).signum() <= 0) {
            throw new IllegalArgumentException("ID, dimensiones y precio deben ser positivos.");
        }
        this.id = id;
        this.ancho = ancho;
        this.largo = largo;
        this.precioPorMetroCuadrado = precioPorMetroCuadrado.setScale(2, RoundingMode.UNNECESSARY);
        if (getPrecioTotal().signum() <= 0) {
            throw new IllegalArgumentException("El precio total debe ser al menos un centavo.");
        }
    }

    public int getId() { return id; }
    public BigDecimal getAncho() { return ancho; }
    public BigDecimal getLargo() { return largo; }
    public BigDecimal getPrecioPorMetroCuadrado() { return precioPorMetroCuadrado; }
    public EstadoStand getEstado() { return estado; }
    public BigDecimal getSuperficie() { return ancho.multiply(largo); }
    public BigDecimal getPrecioTotal() {
        return getSuperficie().multiply(precioPorMetroCuadrado).setScale(2, RoundingMode.HALF_UP);
    }

    public void bloquear() {
        exigirEstado(EstadoStand.DISPONIBLE);
        estado = EstadoStand.BLOQUEADO;
    }

    public void desbloquear() {
        exigirEstado(EstadoStand.BLOQUEADO);
        estado = EstadoStand.DISPONIBLE;
    }

    /** La reserva solo se construye desde el stand, evitando asociaciones duplicadas. */
    public Reserva reservar(int idReserva, Editorial editorial, LocalDate fecha,
                            LocalDate vencimiento, LocalDate limiteLiquidacion) {
        exigirEstado(EstadoStand.DISPONIBLE);
        Reserva nueva = new Reserva(idReserva, editorial, this, fecha, vencimiento, limiteLiquidacion);
        reservaActual = nueva;
        estado = EstadoStand.RESERVADO;
        return nueva;
    }

    // Acceso de paquete: únicamente Reserva coordina estas transiciones.
    void confirmarOcupacion(Reserva reserva) {
        exigirReservaActual(reserva);
        exigirEstado(EstadoStand.RESERVADO);
        if (!reserva.alcanzaMinimo()) {
            throw new IllegalStateException("No se ha cubierto el 15%.");
        }
        estado = EstadoStand.OCUPADO;
    }

    void liberar(Reserva reserva) {
        exigirReservaActual(reserva);
        exigirEstado(EstadoStand.RESERVADO);
        if (!reserva.isVencida()) {
            throw new IllegalStateException("La reserva no ha vencido.");
        }
        reservaActual = null;
        estado = EstadoStand.DISPONIBLE;
    }

    private void exigirEstado(EstadoStand esperado) {
        if (estado != esperado) {
            throw new IllegalStateException("Stand " + id + ": se esperaba " + esperado + ", estado " + estado);
        }
    }

    private void exigirReservaActual(Reserva reserva) {
        if (reservaActual != reserva) {
            throw new IllegalStateException("La reserva no corresponde a la asignación actual.");
        }
    }
}
