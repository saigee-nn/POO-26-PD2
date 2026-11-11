package filey.datos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Reserva {
    private final int id;
    private final Editorial editorial;
    private final Stand stand;
    private final LocalDate fechaCreacion;
    private final LocalDate vencimiento;
    private final LocalDate limiteLiquidacion;
    private final BigDecimal precioTotal;
    private final List<Pago> pagos = new ArrayList<>();
    private boolean vencida;

    Reserva(int id, Editorial editorial, Stand stand, LocalDate fechaCreacion,
            LocalDate vencimiento, LocalDate limiteLiquidacion) {
        if (id <= 0) { throw new IllegalArgumentException("El ID debe ser positivo."); }
        this.id = id;
        this.editorial = Objects.requireNonNull(editorial);
        this.stand = Objects.requireNonNull(stand);
        this.fechaCreacion = Objects.requireNonNull(fechaCreacion);
        this.vencimiento = Objects.requireNonNull(vencimiento);
        this.limiteLiquidacion = Objects.requireNonNull(limiteLiquidacion);
        if (vencimiento.isBefore(fechaCreacion) || vencimiento.isAfter(limiteLiquidacion)) {
            throw new IllegalArgumentException("Las fechas de la reserva no son válidas.");
        }
        this.precioTotal = stand.getPrecioTotal();
    }

    public int getId() { return id; }
    public Editorial getEditorial() { return editorial; }
    public Stand getStand() { return stand; }
    public LocalDate getFechaCreacion() { return fechaCreacion; }
    public LocalDate getVencimiento() { return vencimiento; }
    public LocalDate getLimiteLiquidacion() { return limiteLiquidacion; }
    public BigDecimal getPrecioTotal() { return precioTotal; }
    public List<Pago> getPagos() { return List.copyOf(pagos); }
    public boolean isVencida() { return vencida; }

    public BigDecimal getAbonado() {
        return pagos.stream().map(Pago::getImporte).reduce(new BigDecimal("0.00"), BigDecimal::add);
    }
    public BigDecimal getPendiente() { return precioTotal.subtract(getAbonado()); }
    public BigDecimal getPorcentajePagado() {
        return getAbonado().multiply(new BigDecimal("100")).divide(precioTotal, 2, RoundingMode.HALF_UP);
    }
    public boolean alcanzaMinimo() {
        // Comparación exacta: el porcentaje mostrado redondeado no decide el estado.
        return getAbonado().multiply(new BigDecimal("100"))
                .compareTo(precioTotal.multiply(new BigDecimal("15"))) >= 0;
    }

    public void registrarPago(BigDecimal importe, LocalDate fecha) {
        Pago pago = new Pago(importe, fecha);
        if (fecha.isBefore(fechaCreacion) || (!pagos.isEmpty()
                && fecha.isBefore(pagos.get(pagos.size() - 1).getFecha()))) {
            throw new IllegalArgumentException("No se admiten pagos anteriores a la reserva o al último pago.");
        }
        if (fecha.isAfter(limiteLiquidacion) || vencida
                || (!alcanzaMinimo() && fecha.isAfter(vencimiento))) {
            throw new IllegalStateException("El plazo de pago ha vencido.");
        }
        if (pago.getImporte().compareTo(getPendiente()) > 0) {
            throw new IllegalArgumentException("El pago supera el saldo pendiente.");
        }
        pagos.add(pago);
        if (stand.getEstado() == EstadoStand.RESERVADO && alcanzaMinimo()) {
            stand.confirmarOcupacion(this);
        }
    }

    public boolean vencerSiCorresponde(LocalDate fecha) {
        Objects.requireNonNull(fecha);
        if (!vencida && !alcanzaMinimo() && fecha.isAfter(vencimiento)) {
            vencida = true;
            stand.liberar(this);
            return true;
        }
        return false;
    }
}
