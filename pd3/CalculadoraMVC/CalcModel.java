import java.math.BigDecimal;

public class CalcModel {
    private static final String INITIAL_VALUE = "1";
    private BigDecimal m_total;

    CalcModel() {
        reset();
    }

    public void reset() {
        m_total = new BigDecimal(INITIAL_VALUE);
    }

    public void multiplyBy(String operand) {
        m_total = m_total.multiply(new BigDecimal(operand));
    }

    public void addBy(String operand) {
        m_total = m_total.add(new BigDecimal(operand));
    }

    public void setValue(String value) {
        m_total = new BigDecimal(value);
    }

    public String getValue() {
        return m_total.toPlainString();
    }
}
