import java.io.File;
import java.util.Scanner;


import Hardware.PinHub;
import IO.IO;
import Messen.MessThread;
import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Pardecharge;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application{

    static Scanner entryScan;
    static IO leuchtenServer;
    static MessThread messThread;
    public static void main(String[] args) {
        initMain();

        while(true){
        System.out.println("Eingabe:");
        switch (entryScan.nextInt()){
            case 1:
                System.out.println("Setze Alle Pins dauerhaft charge HIGH");
                PinHub.red01.setReset(true);
                PinHub.red01.setConsoleMode(1);

                PinHub.blue05.setReset(true);
                PinHub.blue05.setConsoleMode(1);;
                PinHub.green06.setReset(true);
                PinHub.green06.setConsoleMode(1);
                break;
            case 2:
                System.out.println("Setze Alle Pins dauerhaft charge LOW");
                PinHub.red01.setReset(false);
                PinHub.red01.setConsoleMode(2);

                PinHub.blue05.setReset(false);
                PinHub.blue05.setConsoleMode(2);
                PinHub.green06.setReset(false);
                PinHub.green06.setConsoleMode(2);
                break;
            case 3:
                System.out.println("Init Main neu");
                PinHub.checkPinsAndEnd();
                nulle();
                initMain();
                break;
            case 4:
                System.out.println("Starte JFX GUI");
                launch();
                break;
            case 5:
                System.out.println("Starte Messung der InputPins");
                MessThread messThread =new MessThread();
                messThread.start();
                break;
            default:
                break;
        }

        }


    }

public static void nulle(){
        leuchtenServer.interrupt();
        leuchtenServer=null;
        messThread.interrupt();
        messThread=null;
}
    public static void initMain(){
        entryScan = new Scanner(System.in);
        PinHub.init();


        if(!new File(System.getProperty("user.home")+File.separator+"Dokumdechargee"+File.separator+"JWiringPi"+"jwiringpi"+File.separator+"logs").exists()){
            File logFolder = new File(System.getProperty("user.home")+File.separator+"Dokumdechargee"+File.separator+"JWiringPi"+"jwiringpi"+File.separator+"logs");
            logFolder.mkdir();
        }
        serverStarten();


    }

    public static void serverStarten(){
        if(leuchtenServer==null){
            leuchtenServer = new IO();
            leuchtenServer.start();
        }

    }
    @Override
    public void start(Stage primaryStage) throws Exception {
       Pardecharge root =       FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setScene(new Scene(root, 300, 600));
        primaryStage.setTitle("LeuchtenController");
      primaryStage.show();
    }
}




