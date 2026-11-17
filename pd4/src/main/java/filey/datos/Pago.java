package filey.datos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

public final class Pago {
    private final BigDecimal importe;
    private final LocalDate fecha;

    public Pago(BigDecimal importe, LocalDate fecha) {
        Objects.requireNonNull(importe, "El importe es obligatorio.");
        if (importe.signum() <= 0) {
            throw new IllegalArgumentException("El pago debe ser positivo.");
        }
        // Se rechazan fracciones menores que un centavo; no se redondean pagos.
        this.importe = importe.setScale(2, RoundingMode.UNNECESSARY);
        this.fecha = Objects.requireNonNull(fecha, "La fecha es obligatoria.");
    }

    public BigDecimal getImporte() { return importe; }
    public LocalDate getFecha() { return fecha; }
}
