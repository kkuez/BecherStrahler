

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
    Button RotButton;
    @FXML
    Button GruenButton;
    @FXML
    Button BlauButton;
    @FXML
    Button debugButton;
    @FXML
    Button resettenButton;
    @FXML
    TextField blauAufTextf;
    @FXML
    TextField gruenAufTextf;
    @FXML
    TextField rotAufTextf;
    @FXML
    TextField blauEntTextf;
    @FXML
    TextField gruenEntTextf;
    @FXML
    TextField rotEntTextf;
    @FXML
    Label debugLabel;


    public void debug(){
        StringBuilder logLabelText=new StringBuilder();
        logLabelText.append("Pin 1:\nAufladeZeit: "+PinHub.rot01.getAufladezeit()+"\nEntladeZeit:"+PinHub.rot01.getEntladezeit()+"\nSpannung: "+PinHub.rot01.getSpannung());
        logLabelText.append("\n\nPin 5:\nAufladeZeit: "+PinHub.blau05.getAufladezeit()+"\nEntladeZeit:"+PinHub.blau05.getEntladezeit()+"\nSpannung: "+PinHub.blau05.getSpannung());
        logLabelText.append("\n\nPin 6:\nAufladeZeit: "+PinHub.gruen06.getAufladezeit()+"\nEntladeZeit:"+PinHub.gruen06.getEntladezeit()+"\nSpannung: "+PinHub.gruen06.getSpannung());
        Logging.refreshLogLabel(logLabelText.toString());
        debugLabel.setText(Logging.logLabel);
    }

    public void resetten(){
        PinHub.pinsPruefenUndBeenden();
    }

    public void pinSetzenEins() {
        PinHub.rot01.setAufladezeit(Integer.parseInt(rotAufTextf.getText()));
        PinHub.rot01.setEntladezeit(Integer.parseInt(rotEntTextf.getText()));
        //   PinHub.rot01.refresh();
        PinHub.rot01.setReset(false);

    }
    public void pinSetzenFuenf() {
        PinHub.blau05.setAufladezeit(Integer.parseInt(blauAufTextf.getText()));
        PinHub.blau05.setEntladezeit(Integer.parseInt(blauEntTextf.getText()));
        //   PinHub.rot01.refresh();
        PinHub.blau05.setReset(false);
    }
    public void pinSetzenSechs() {

        PinHub.gruen06.setAufladezeit(Integer.parseInt(gruenAufTextf.getText()));
        PinHub.gruen06.setEntladezeit(Integer.parseInt(gruenEntTextf.getText()));
        //   PinHub.rot01.refresh();
        PinHub.gruen06.setReset(false);

    }

    public void testen(){
        System.out.println("asd");
    }
}
