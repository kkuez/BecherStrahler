

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PinHub {

    static Pin rot01 = new Pin(1);
    static Pin blau05=new Pin(5);
    static Pin gruen06=new Pin(6);
    static List<MessZustand> MessListeFürRot=new ArrayList<>();
    static List<MessZustand> defaultListeFürRot=new ArrayList<>();
    static List<MessZustand> MessListeFuerBlau=new ArrayList<>();
    static List<MessZustand> defaultListeFuerBlau=new ArrayList<>();
    static List<MessZustand> defaultListeFuerGruen=new ArrayList<>();
    static List<MessZustand> MessListeFuerGruen=new ArrayList<>();

    static Object lockRot=new Object();
    static Object lockBlau=new Object();
    static Object lockGruen=new Object();


    public static void init(){
        rot01.start();
        gruen06.start();
        blau05.start();
        fillDefaultListeRot();
        fillDefaultListeBlauUndGruen();
        defaultListeFürRot=defaultlisteVerarbeiten(defaultListeFürRot);
        defaultListeFuerBlau=defaultlisteVerarbeiten(defaultListeFuerBlau);
        defaultListeFuerGruen=defaultlisteVerarbeiten(defaultListeFuerGruen);
        setMessListe(1, defaultlisteVerarbeiten(defaultListeFürRot));
        setMessListe(5, defaultlisteVerarbeiten(defaultListeFuerBlau));
        setMessListe(6, defaultlisteVerarbeiten(defaultListeFuerGruen));
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

        //Gucken wo grösster Abstand, dann kopieren und einfügen wenn Liste unter 100

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
        //Gucken wo kleinster Abstand, dann löschen wenn Liste über 100



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

            for(MessZustand m : getMessListeFürRot()){
                writer.write("\nrot;auf:"+m.aufladezeit+";ent:"+m.entladezeit+";LDR:"+m.messwertLDR+";perd:"+m.periodendauer+";diffv:"+m.differenzInMessungen+";\n");
            }
            for(MessZustand m : getMessListeFuerBlau()){
                writer.write("\nblau;auf:"+m.aufladezeit+";ent:"+m.entladezeit+";LDR:"+m.messwertLDR+";perd:"+m.periodendauer+";diffv:"+m.differenzInMessungen+";\n");
            }
            for(MessZustand m : getMessListeFuerGruen()){
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
        MessListeFürRot = new ArrayList<>();
        MessListeFuerBlau = new ArrayList<>();
        MessListeFuerGruen = new ArrayList<>();
        try(Scanner scan= new Scanner(new File("geparsteListen.txt"))){

            while(scan.hasNextLine()){
                currentLine=scan.nextLine();
                if(currentLine.startsWith("rot;")){
                    getMessListeFürRot().add(parseLineToMessZustand(currentLine));
                }
                if(currentLine.startsWith("blau;")){
                    getMessListeFuerBlau().add(parseLineToMessZustand(currentLine));
                }
                if(currentLine.startsWith("gruen;")){
                    getMessListeFuerGruen().add(parseLineToMessZustand(currentLine));
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
        defaultListeFürRot.add(new MessZustand(2,4));
        defaultListeFürRot.add(new MessZustand(1,1));
        defaultListeFürRot.add(new MessZustand(1,3));
        defaultListeFürRot.add(new MessZustand(1,4));
        defaultListeFürRot.add(new MessZustand(1,5));
        defaultListeFürRot.add(new MessZustand(1,6));
        defaultListeFürRot.add(new MessZustand(1,7));
        defaultListeFürRot.add(new MessZustand(1,8));
        defaultListeFürRot.add(new MessZustand(0,1));
        defaultListeFürRot.add(new MessZustand(0,2));
        defaultListeFürRot.add(new MessZustand(0,3));
        defaultListeFürRot.add(new MessZustand(0,4));
        defaultListeFürRot.add(new MessZustand(0,6));
        defaultListeFürRot.add(new MessZustand(0,8));
        defaultListeFürRot.add(new MessZustand(0,9));
        defaultListeFürRot.add(new MessZustand(0,11));
        defaultListeFürRot.add(new MessZustand(0,13));
        defaultListeFürRot.add(new MessZustand(0,15));

    }
    public static void fillDefaultListeBlauUndGruen(){

        defaultListeFuerBlau.add(new MessZustand(2,0));
        defaultListeFuerBlau.add(new MessZustand(1,1));
        defaultListeFuerBlau.add(new MessZustand(1,2));
        defaultListeFuerBlau.add(new MessZustand(1,3));
        defaultListeFuerBlau.add(new MessZustand(1,4));
        defaultListeFuerBlau.add(new MessZustand(1,5));
        defaultListeFuerBlau.add(new MessZustand(1,6));
        defaultListeFuerBlau.add(new MessZustand(0,1));
        defaultListeFuerBlau.add(new MessZustand(0,2));
        defaultListeFuerBlau.add(new MessZustand(0,3));
        defaultListeFuerBlau.add(new MessZustand(0,4));
        defaultListeFuerBlau.add(new MessZustand(0,6));
        defaultListeFuerBlau.add(new MessZustand(0,8));
        defaultListeFuerBlau.add(new MessZustand(0,9));
        defaultListeFuerBlau.add(new MessZustand(0,11));
        defaultListeFuerBlau.add(new MessZustand(0,13));
        defaultListeFuerBlau.add(new MessZustand(0,15));

        defaultListeFuerGruen.add(new MessZustand(2,1));
        defaultListeFuerGruen.add(new MessZustand(1,1));
        defaultListeFuerGruen.add(new MessZustand(1,2));
        defaultListeFuerGruen.add(new MessZustand(1,3));
        defaultListeFuerGruen.add(new MessZustand(1,4));
        defaultListeFuerGruen.add(new MessZustand(1,5));
        defaultListeFuerGruen.add(new MessZustand(1,6));
        defaultListeFuerGruen.add(new MessZustand(0,1));
        defaultListeFuerGruen.add(new MessZustand(0,2));
        defaultListeFuerGruen.add(new MessZustand(0,3));
        defaultListeFuerGruen.add(new MessZustand(0,4));
        defaultListeFuerGruen.add(new MessZustand(0,6));
        defaultListeFuerGruen.add(new MessZustand(0,8));
        defaultListeFuerGruen.add(new MessZustand(0,9));
        defaultListeFuerGruen.add(new MessZustand(0,11));
        defaultListeFuerGruen.add(new MessZustand(0,13));
        defaultListeFuerGruen.add(new MessZustand(0,15));


    }

    // Getter Setter
    public static List<MessZustand> getMessListeFürRot() {

        synchronized (lockRot) {
            if(MessListeFürRot.size()!=0){
                return MessListeFürRot;
            }
        }
        return defaultListeFürRot;
    }


    public static void setMessListe(int pinNummer, List<MessZustand> MessListe) {
        switch (pinNummer){
            case 1:
                synchronized (lockRot) {
                    PinHub.MessListeFürRot = MessListe;
                }
                break;

            case 5:
                synchronized (lockBlau) {
                    PinHub.MessListeFuerBlau = MessListe;
                }
                break;

            case 6: synchronized (lockGruen) {
                PinHub.MessListeFuerGruen = MessListe;
            }

                break;

        }

    }

    public static List<MessZustand> getMessListeFuerBlau() {
        synchronized (lockBlau) {
            if(MessListeFuerBlau.size()!=0){
                return MessListeFuerBlau;
            }
        }
        return defaultListeFuerBlau;
    }

    public static void setMessListeFuerBlau(List<MessZustand> MessListeFuerBlau) {

        synchronized (lockBlau) {
            PinHub.MessListeFuerBlau = MessListeFuerBlau;
        }
    }
    public static List<MessZustand> getMessListeFuerGruen() {
        synchronized (lockGruen) {
            if(MessListeFuerGruen.size()!=0){
                return MessListeFuerGruen;
            }
        }
        return defaultListeFuerGruen;
    }

    public static void setMessListeFuerGruen(List<MessZustand> MessListeFuerGruen) {
        synchronized (lockGruen) {

            PinHub.MessListeFuerGruen = MessListeFuerGruen;
        }
    }
}
