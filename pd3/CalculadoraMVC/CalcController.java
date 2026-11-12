import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CalcController {
    private final CalcModel m_model;
    private final CalcView m_view;

    CalcController(CalcModel model, CalcView view) {
        m_model = model;
        m_view = view;

        view.addAdditionListener(new AdditionListener());
        view.addMultiplyListener(new MultiplyListener());
        view.addClearListener(new ClearListener());
    }

    class AdditionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String userInput = "";
            try {
                userInput = m_view.getUserInput();
                m_model.addBy(userInput);
                m_view.setTotal(m_model.getValue());
            } catch (NumberFormatException ex) {
                m_view.showError("Bad input: '" + userInput + "'");
            }
        }
    }

    class MultiplyListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String userInput = "";
            try {
                userInput = m_view.getUserInput();
                m_model.multiplyBy(userInput);
                m_view.setTotal(m_model.getValue());
            } catch (NumberFormatException ex) {
                m_view.showError("Bad input: '" + userInput + "'");
            }
        }
    }

    class ClearListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            m_model.reset();
            m_view.reset();
        }
    }
}
