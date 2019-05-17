package Messen;

import Hardware.Pin;
import Hardware.PinHub;

import java.lang.Thread;
import java.util.*;

public class MessThread extends Thread {

    MessProcess messProcess;


    List<MessZustand> messZustandsListe=new ArrayList<>();

    public MessThread(){
         messProcess= new MessProcess();
         messProcess.start();
    }

public double einmalAbrufen(MessProcess messProcess, int pin){
    double zurueck=0;
                        while(zurueck==0){
                            try {//Line 1 ist der LDR
                                zurueck = Double.parseDouble(messProcess.getMessLinesFuerJedenEingang().get(1).substring(messProcess.getMessLinesFuerJedenEingang().get(1).indexOf("(") + 1, messProcess.getMessLinesFuerJedenEingang().get(1).indexOf(".") + 4));
                            }catch (Exception e){
                                System.out.println("Fehler beim Parsen von Pin 0"+pin);
                                e.printStackTrace();
                            }
                            }
                            messProcess.getMessLinesFuerJedenEingang().set(pin, "0");
    return zurueck;
}
public void kalibieren(int pinnummer){
    Pin aktuellerPin=new Pin(pinnummer);
    switch (pinnummer){
        case 1:
            aktuellerPin= PinHub.rot01;
            break;
        case 5:
            aktuellerPin= PinHub.blau05;
            break;
        case 6:
            aktuellerPin= PinHub.gruen06;
            break;

    }

    System.out.println("Warte auf erste komplette Messung...");

    while(messProcess.getMessLinesFuerJedenEingang().size()<8){

    }
    double messungMax=0;
    double messungMin=0;
    double messungdifferenz;
    double messungMittelwert;
    double messungAktuellerWert=0;
    double[] messungsArrayAlleWerte=new double[20];

  for(int i =0;i<20;i++) {
        for (int a = 0; a < 20; a++) {
            if(a+i<23) {
                for (int b = 0; b < 10; b++) {      //10 Messungen um Differenz raus zuziehen

                    messungAktuellerWert = einmalAbrufen(messProcess, pinnummer);
                    messungsArrayAlleWerte[b] = messungAktuellerWert;
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

                Arrays.sort(messungsArrayAlleWerte);
                double[] gefilterterArray = new double[8];
                for (int c = 0; c < 8; c++) {
                    gefilterterArray[c] = messungsArrayAlleWerte[c + 2];
                }

                for (double d : gefilterterArray) {
                    if (d == 0) {
                        messungMax = messungAktuellerWert;
                        messungMin = messungAktuellerWert;
                    } else {
                        if (messungAktuellerWert < messungMin) {
                            messungMin = messungAktuellerWert;
                        }
                        if (messungAktuellerWert > messungMax) {
                            messungMax = messungAktuellerWert;
                        }
                    }
                }

                messungdifferenz = messungMax - messungMin;


                MessZustand messZustand = new MessZustand();

                aktuellerPin.pinSetzen(i, a);

                messZustand = new MessZustand(i, a, messungMax, messungdifferenz);


                if (messZustand.messwertLDR != 0) {
                    messZustandsListe.add(messZustand);
                }

            }
        }
    }

    System.out.println("Messung beendet\nVerarbeite Liste...");
    messZustandsListe= PinHub.listeVerarbeiten(messZustandsListe);
    PinHub.setKalibrierteMessListe(pinnummer,messZustandsListe);

    PinHub.pinsPruefenUndBeenden();
}





    public void run(){

       kalibieren(1);
       kalibieren(5);
       kalibieren(6);
       PinHub.speicherKalibrierteListen();
        messProcess=null;
       System.out.println("Kalibrierung beendet");

    }
}
