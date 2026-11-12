import java.math.BigDecimal;

public class CalcModel {
    // Valor inicial para la multiplicacion acumulada.
    private static final String INITIAL_VALUE = "1";

    // BigDecimal acepta enteros grandes y numeros decimales.
    private BigDecimal m_total;

    CalcModel() {
        reset();
    }

    // Reinicia el valor de la calculadora a 1.
    public void reset() {
        m_total = new BigDecimal(INITIAL_VALUE);
    }

    // Multiplica el resultado acumulado por el numero ingresado.
    public void multiplyBy(String operand) {
        m_total = m_total.multiply(new BigDecimal(operand));
    }

    // Establece el valor de la calculadora.
    public void setValue(String value) {
        m_total = new BigDecimal(value);
    }

    // Devuelve el resultado como texto sin notacion cientifica.
    public String getValue() {
        return m_total.toPlainString();
    }
}
