package filey.datos;

import java.time.LocalDate;
import java.util.Objects;

/** Valores elegidos al crear el sistema; las fechas límite son inclusivas. */
public final class Configuracion {
    private final LocalDate limiteDescuento;
    private final LocalDate limiteLiquidacion;
    private final LocalDate limiteReserva;
    private final int diasParaPagarReserva;
    private final int diasAnticipacionAviso;

    public Configuracion(LocalDate limiteDescuento, LocalDate limiteLiquidacion,
                         LocalDate limiteReserva, int diasParaPagarReserva,
                         int diasAnticipacionAviso) {
        this.limiteDescuento = Objects.requireNonNull(limiteDescuento);
        this.limiteLiquidacion = Objects.requireNonNull(limiteLiquidacion);
        this.limiteReserva = Objects.requireNonNull(limiteReserva);
        if (limiteDescuento.isAfter(limiteLiquidacion)
                || limiteReserva.isAfter(limiteLiquidacion)
                || diasParaPagarReserva <= 0 || diasAnticipacionAviso < 0) {
            throw new IllegalArgumentException("La configuración de plazos no es válida.");
        }
        this.diasParaPagarReserva = diasParaPagarReserva;
        this.diasAnticipacionAviso = diasAnticipacionAviso;
    }

    public LocalDate getLimiteDescuento() { return limiteDescuento; }
    public LocalDate getLimiteLiquidacion() { return limiteLiquidacion; }
    public LocalDate getLimiteReserva() { return limiteReserva; }
    public int getDiasParaPagarReserva() { return diasParaPagarReserva; }
    public int getDiasAnticipacionAviso() { return diasAnticipacionAviso; }
}
