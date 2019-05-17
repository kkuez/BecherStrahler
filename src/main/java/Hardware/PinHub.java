package Hardware;

import Messen.MessZustand;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PinHub {

    public static Pin rot01 = new Pin(1);
    public static Pin blau05=new Pin(5);
    public static Pin gruen06=new Pin(6);
    static List<MessZustand> kalibrierteMessListeFuerRot=new ArrayList<>();
    static List<MessZustand> defaultListeFuerRot=new ArrayList<>();
    static List<MessZustand> kalibrierteMessListeFuerBlau=new ArrayList<>();
    static List<MessZustand> defaultListeFuerBlau=new ArrayList<>();
    static List<MessZustand> defaultListeFuerGruen=new ArrayList<>();
    static List<MessZustand> kalibrierteMessListeFuerGruen=new ArrayList<>();

    static Object lockRot=new Object();
    static Object lockBlau=new Object();
    static Object lockGruen=new Object();

     public PinHub(){
 init();
    }
public static void init(){
    rot01.start();
    gruen06.start();
    blau05.start();
    fillDefaultListeRot();
    fillDefaultListeBlauUndGruen();
    setKalibrierteMessListe(1, defaultlisteVerarbeiten(defaultListeFuerRot));
    setKalibrierteMessListe(5, defaultlisteVerarbeiten(defaultListeFuerBlau));
    setKalibrierteMessListe(6, defaultlisteVerarbeiten(defaultListeFuerGruen));
    if(new File("geparsteListen.txt").exists()){
        leseKalibrierteListen();

    }

     }
    public static List<MessZustand> defaultlisteVerarbeiten(List<MessZustand> messZustandsListerein){
        List<MessZustand> listeRaus;
        listeRaus=messZustandsListerein;
       while (listeRaus.size()<100){

           for (int i =messZustandsListerein.size()-1;i>1;i--){
               listeRaus.add(i,messZustandsListerein.get(i));
               if(listeRaus.size()>=100){
                   break;
               }
           }
           messZustandsListerein=listeRaus;
       }

        return listeRaus;
    }
    public static List<MessZustand> listeVerarbeiten(List<MessZustand> messZustandsListerein){
     /*  for(int i = 0;i<messZustandsListerein.size();i++){
            if(messZustandsListerein.get(i).periodendauer>15){
                messZustandsListerein.remove(i);
            }
        }*/
       /* Collections.sort(messZustandsListerein, new Comparator<MessZustand>(){
            public int compare(MessZustand o1, MessZustand o2){
                if(o1.differenzInMessungen == o2.differenzInMessungen)
                    return 0;
                return o1.differenzInMessungen < o2.differenzInMessungen ? -1 : 1;
            }
        });

        while(messZustandsListerein.size()>100){
            messZustandsListerein.remove(messZustandsListerein.size()-1);
        }
*/
        Collections.sort(messZustandsListerein, new Comparator<MessZustand>(){
            public int compare(MessZustand o1, MessZustand o2){
                if(o1.messwertLDR == o2.messwertLDR)
                    return 0;
                return o1.messwertLDR < o2.messwertLDR ? -1 : 1;
            }
        });

        //Gucken wo groesster Abstand, dann kopieren und einfuegen wenn Liste unter 100

        while(messZustandsListerein.size()<100){
            double groessterAbstand=0;
            int index=0;
            for(int i = 0 ; i<messZustandsListerein.size()-1;i++){
                double aktuellerAbstand = messZustandsListerein.get(i+1).messwertLDR-messZustandsListerein.get(i).messwertLDR;
                if(aktuellerAbstand>groessterAbstand){
                    groessterAbstand=aktuellerAbstand;
                    index=i;
                }
            }
            messZustandsListerein.add(index+1, messZustandsListerein.get(index));
        }
        //Gucken wo kleinster Abstand, dann loeschen wenn Liste ueber 100



        while(messZustandsListerein.size()>100){

            double kleinsterAbstand=100;
            int index=0;
            for(int i = 0 ; i<messZustandsListerein.size()-1;i++){
                if(messZustandsListerein.get(i+1).messwertLDR-messZustandsListerein.get(i).messwertLDR<kleinsterAbstand){
                    index=i;
                }
            }
            messZustandsListerein.remove(index);
        }

        return messZustandsListerein;
    }
    public static void speicherKalibrierteListen(){
        try(BufferedWriter writer=new BufferedWriter(new FileWriter("geparsteListen.txt"))) {

            for(MessZustand m : getKalibrierteMessListeFuerRot()){
                writer.write("\nrot;auf:"+m.aufladezeit+";ent:"+m.entladezeit+";LDR:"+m.messwertLDR+";perd:"+m.periodendauer+";diffv:"+m.differenzInMessungen+";\n");
            }
            for(MessZustand m : getKalibrierteMessListeFuerBlau()){
                writer.write("\nblau;auf:"+m.aufladezeit+";ent:"+m.entladezeit+";LDR:"+m.messwertLDR+";perd:"+m.periodendauer+";diffv:"+m.differenzInMessungen+";\n");
            }
            for(MessZustand m : getKalibrierteMessListeFuerGruen()){
                writer.write("\ngruen;auf:"+m.aufladezeit+";ent:"+m.entladezeit+";LDR:"+m.messwertLDR+";perd:"+m.periodendauer+";diffv:"+m.differenzInMessungen+";");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static MessZustand parseLineToMessZustand(String rein){
         MessZustand raus= new MessZustand();
         rein=rein.substring(rein.indexOf(";")+1);
        raus.aufladezeit=Integer.parseInt(rein.substring(rein.indexOf(":", 0),rein.indexOf(";", 0)).replace(":", ""));
        rein=rein.substring(rein.indexOf(";")+1);
        raus.entladezeit=Integer.parseInt(rein.substring(rein.indexOf(":", 0),rein.indexOf(";", 0)).replace(":", ""));
        rein=rein.substring(rein.indexOf(";")+1);
        raus.messwertLDR=Double.parseDouble(rein.substring(rein.indexOf(":", 0),rein.indexOf(";", 0)).replace(":", ""));
        rein=rein.substring(rein.indexOf(";")+1);
        raus.periodendauer=Integer.parseInt(rein.substring(rein.indexOf(":", 0),rein.indexOf(";", 0)).replace(":", ""));
        rein=rein.substring(rein.indexOf(";")+1);
        raus.differenzInMessungen=Double.parseDouble(rein.substring(rein.indexOf(":", 0),rein.indexOf(";", 0)).replace(":", ""));
        return raus;
    }
    public static void leseKalibrierteListen(){
        String currentLine="";
        kalibrierteMessListeFuerRot = new ArrayList<>();
        kalibrierteMessListeFuerBlau = new ArrayList<>();
        kalibrierteMessListeFuerGruen = new ArrayList<>();
         try(Scanner scan= new Scanner(new File("geparsteListen.txt"))){

             while(scan.hasNextLine()){
                 currentLine=scan.nextLine();
                 if(currentLine.startsWith("rot;")){
                     getKalibrierteMessListeFuerRot().add(parseLineToMessZustand(currentLine));
                 }
                 if(currentLine.startsWith("blau;")){
                     getKalibrierteMessListeFuerBlau().add(parseLineToMessZustand(currentLine));
                 }
                 if(currentLine.startsWith("gruen;")){
                     getKalibrierteMessListeFuerGruen().add(parseLineToMessZustand(currentLine));
                 }
             }
         }catch (Exception e){
             e.printStackTrace();
         }
  }


    public static void pinsPruefenUndBeenden(){
        rot01.resetten();
        gruen06.resetten();
        blau05.resetten();
    }

    //Sonstige
    public static void fillDefaultListeRot(){
         defaultListeFuerRot.add(new MessZustand(1,0));
         defaultListeFuerRot.add(new MessZustand(0,0));
         defaultListeFuerRot.add(new MessZustand(0,1));
         defaultListeFuerRot.add(new MessZustand(0,2));
         defaultListeFuerRot.add(new MessZustand(0,3));
         defaultListeFuerRot.add(new MessZustand(0,4));
         defaultListeFuerRot.add(new MessZustand(0,5));
         defaultListeFuerRot.add(new MessZustand(0,6));
         defaultListeFuerRot.add(new MessZustand(0,7));
         defaultListeFuerRot.add(new MessZustand(0,8));
         defaultListeFuerRot.add(new MessZustand(0,9));
         defaultListeFuerRot.add(new MessZustand(0,10));
         defaultListeFuerRot.add(new MessZustand(0,11));
         defaultListeFuerRot.add(new MessZustand(0,12));
         defaultListeFuerRot.add(new MessZustand(0,13));
    }
    public static void fillDefaultListeBlauUndGruen(){

         defaultListeFuerBlau.add(new MessZustand(9,4));
         defaultListeFuerBlau.add(new MessZustand(8,4));
         defaultListeFuerBlau.add(new MessZustand(7,4));
         defaultListeFuerBlau.add(new MessZustand(6,4));
         defaultListeFuerBlau.add(new MessZustand(5,4));
         defaultListeFuerBlau.add(new MessZustand(4,4));
         defaultListeFuerBlau.add(new MessZustand(3,4));
         defaultListeFuerBlau.add(new MessZustand(2,4));
         defaultListeFuerBlau.add(new MessZustand(1,4));
         defaultListeFuerBlau.add(new MessZustand(0,4));
         defaultListeFuerBlau.add(new MessZustand(0,6));
         defaultListeFuerBlau.add(new MessZustand(0,8));
         defaultListeFuerBlau.add(new MessZustand(0,11));
         defaultListeFuerBlau.add(new MessZustand(0,13));
         defaultListeFuerBlau.add(new MessZustand(0,15));
         defaultListeFuerBlau.add(new MessZustand(0,18));
         defaultListeFuerBlau.add(new MessZustand(0,20));

        defaultListeFuerGruen.add(new MessZustand(7,6));
        defaultListeFuerGruen.add(new MessZustand(6,7));
        defaultListeFuerGruen.add(new MessZustand(5,7));
        defaultListeFuerGruen.add(new MessZustand(3,7));
        defaultListeFuerGruen.add(new MessZustand(2,7));
        defaultListeFuerGruen.add(new MessZustand(1,7));
        defaultListeFuerGruen.add(new MessZustand(1,9));
        defaultListeFuerGruen.add(new MessZustand(1,12));
        defaultListeFuerGruen.add(new MessZustand(0,3));
        defaultListeFuerGruen.add(new MessZustand(0,5));
        defaultListeFuerGruen.add(new MessZustand(0,7));
        defaultListeFuerGruen.add(new MessZustand(0,9));
        defaultListeFuerGruen.add(new MessZustand(0,11));
        defaultListeFuerGruen.add(new MessZustand(0,13));
        defaultListeFuerGruen.add(new MessZustand(0,16));
        defaultListeFuerGruen.add(new MessZustand(0,19));
    }

    // Getter Setter
    public static List<MessZustand> getKalibrierteMessListeFuerRot() {
        synchronized (lockRot) {
        return kalibrierteMessListeFuerRot;
    }
     }

    public static void setKalibrierteMessListe(int pinNummer, List<MessZustand> kalibrierteMessListe) {
         switch (pinNummer){
             case 1:
                 synchronized (lockRot) {
                     PinHub.kalibrierteMessListeFuerRot = kalibrierteMessListe;
                 }
                 break;

             case 5:
                 synchronized (lockBlau) {
                     PinHub.kalibrierteMessListeFuerBlau = kalibrierteMessListe;
                 }
                 break;

             case 6: synchronized (lockGruen) {
                 PinHub.kalibrierteMessListeFuerGruen = kalibrierteMessListe;
             }

                 break;

         }

    }

    public static List<MessZustand> getKalibrierteMessListeFuerBlau() {
        synchronized (lockBlau) {
            return kalibrierteMessListeFuerBlau;
        }
    }

    public static void setKalibrierteMessListeFuerBlau(List<MessZustand> kalibrierteMessListeFuerBlau) {

            synchronized (lockBlau) {
                PinHub.kalibrierteMessListeFuerBlau = kalibrierteMessListeFuerBlau;
            }
    }
    public static List<MessZustand> getKalibrierteMessListeFuerGruen() {
        synchronized (lockGruen) {
            return kalibrierteMessListeFuerGruen;
        }
    }

    public static void setKalibrierteMessListeFuerGruen(List<MessZustand> kalibrierteMessListeFuerGruen) {
            synchronized (lockGruen) {

                PinHub.kalibrierteMessListeFuerGruen = kalibrierteMessListeFuerGruen;
            }
        }
}
