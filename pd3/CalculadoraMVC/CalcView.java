import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

class CalcView extends JFrame {
    private static final String INITIAL_VALUE = "1";

    private final JTextField m_userInputTf = new JTextField(5);
    private final JTextField m_totalTf = new JTextField(20);
    private final JButton m_addBtn = new JButton("Add");
    private final JButton m_multiplyBtn = new JButton("Multiply");
    private final JButton m_clearBtn = new JButton("Clear");
    private final CalcModel m_model;

    CalcView(CalcModel model) {
        m_model = model;
        m_model.setValue(INITIAL_VALUE);

        m_totalTf.setText(m_model.getValue());
        m_totalTf.setEditable(false);

        JPanel content = new JPanel(new FlowLayout());
        content.add(new JLabel("Input"));
        content.add(m_userInputTf);
        content.add(m_addBtn);
        content.add(m_multiplyBtn);
        content.add(new JLabel("Total"));
        content.add(m_totalTf);
        content.add(m_clearBtn);

        setContentPane(content);
        pack();
        setTitle("Simple Calc - MVC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    void reset() {
        m_totalTf.setText(INITIAL_VALUE);
    }

    String getUserInput() {
        return m_userInputTf.getText();
    }

    void setTotal(String newTotal) {
        m_totalTf.setText(newTotal);
    }

    void showError(String errMessage) {
        JOptionPane.showMessageDialog(this, errMessage);
    }

    void addAdditionListener(ActionListener listener) {
        m_addBtn.addActionListener(listener);
    }

    void addMultiplyListener(ActionListener listener) {
        m_multiplyBtn.addActionListener(listener);
    }

    void addClearListener(ActionListener listener) {
        m_clearBtn.addActionListener(listener);
    }
}
