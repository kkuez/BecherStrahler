package IO;

import Hardware.PinHub;
import javafx.fxml.FXML;
        import javafx.scene.control.Button;
        import javafx.scene.control.Label;
        import javafx.scene.control.TextField;
        import javafx.scene.layout.GridPane;


public class Controller {
    @FXML
    GridPane gridPHaupt;
    @FXML
    GridPane gridPOber;
    @FXML
    Button RedButton;
    @FXML
    Button GreenButton;
    @FXML
    Button BlueButton;
    @FXML
    Button debugButton;
    @FXML
    Button resetButton;
    @FXML
    TextField BlueChargeTextf;
    @FXML
    TextField greenChargeTextf;
    @FXML
    TextField redChargeTextf;
    @FXML
    TextField BlueDechargeTextf;
    @FXML
    TextField greenDechargeTextf;
    @FXML
    TextField redDechargeTextf;
    @FXML
    Label debugLabel;


    public void debug(){
        StringBuilder logLabelText=new StringBuilder();
        logLabelText.append("Pin 1:\nChargeladeZeit: "+ PinHub.red01.getChargeTime()+"\nDechargeladeZeit:"+ PinHub.red01.getDechargeTime()+"\nVoltnnung: "+ PinHub.red01.getVoltnnung());
        logLabelText.append("\n\nPin 5:\nChargeladeZeit: "+ PinHub.blue05.getChargeTime()+"\nDechargeladeZeit:"+ PinHub.blue05.getDechargeTime()+"\nVoltnnung: "+ PinHub.blue05.getVoltnnung());
        logLabelText.append("\n\nPin 6:\nChargeladeZeit: "+ PinHub.green06.getChargeTime()+"\nDechargeladeZeit:"+ PinHub.green06.getDechargeTime()+"\nVoltnnung: "+ PinHub.green06.getVoltnnung());
        Logging.Logging.refreshLogLabel(logLabelText.toString());
        debugLabel.setText(Logging.Logging.logLabel);
    }

    public void reset(){
        PinHub.checkPinsAndEnd();
    }

    public void pinSetOne() {
        PinHub.red01.setChargeTime(Integer.parseInt(redChargeTextf.getText()));
        PinHub.red01.setDechargeTime(Integer.parseInt(redDechargeTextf.getText()));
        //   PinHub.red01.refresh();
        PinHub.red01.setReset(false);

    }
    public void pinSetFive() {
        PinHub.blue05.setChargeTime(Integer.parseInt(BlueChargeTextf.getText()));
        PinHub.blue05.setDechargeTime(Integer.parseInt(BlueDechargeTextf.getText()));
        //   PinHub.red01.refresh();
        PinHub.blue05.setReset(false);
    }
    public void pinSetSix() {

        PinHub.green06.setChargeTime(Integer.parseInt(greenChargeTextf.getText()));
        PinHub.green06.setDechargeTime(Integer.parseInt(greenDechargeTextf.getText()));
        //   PinHub.red01.refresh();
        PinHub.green06.setReset(false);

    }

    public void testen(){
        System.out.println("asd");
    }
}
