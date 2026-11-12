public class CalcMVC {
    public static void main(String[] args) {
        CalcModel model = new CalcModel();
        CalcView view = new CalcView(model);
        new CalcController(model, view);
        view.setVisible(true);
    }
}
