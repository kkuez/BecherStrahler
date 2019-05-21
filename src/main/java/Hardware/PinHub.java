package Hardware;

import Messen.MessZustand;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class PinHub {

    public static Pin red01 = new Pin(1);
    public static Pin blue05=new Pin(5);
    public static Pin green06=new Pin(6);
    static List<MessZustand> calibratedCalculateListForRed=new ArrayList<>();
    static List<MessZustand> defaultListForRed=new ArrayList<>();
    static List<MessZustand> calibratedCalculateListForBlue=new ArrayList<>();
    static List<MessZustand> defaultListForBlue=new ArrayList<>();
    static List<MessZustand> defaultListForGreen=new ArrayList<>();
    static List<MessZustand> calibratedCalculateListForGreen=new ArrayList<>();

    static Object lockRed=new Object();
    static Object lockBlue=new Object();
    static Object lockGreen=new Object();

     public PinHub(){
 init();
    }
public static void init(){
    red01.start();
    green06.start();
    blue05.start();
    fillDefaultListRed();
    fillDefaultListBlueAndGreen();
    setCalibratedCalculateList(1, defaultlistProcessing(defaultListForRed));
    setCalibratedCalculateList(5, defaultlistProcessing(defaultListForBlue));
    setCalibratedCalculateList(6, defaultlistProcessing(defaultListForGreen));
    if(new File("geparsteListn.txt").exists()){
        readCalibratedLists();

    }

     }
    public static List<MessZustand> defaultlistProcessing(List<MessZustand> meassuredStatusListinto){
        List<MessZustand> listOut;
        listOut=meassuredStatusListinto;
       while (listOut.size()<100){

           for (int i =meassuredStatusListinto.size()-1;i>1;i--){
               listOut.add(i,meassuredStatusListinto.get(i));
               if(listOut.size()>=100){
                   break;
               }
           }
           meassuredStatusListinto=listOut;
       }

        return listOut;
    }
    public static List<MessZustand> processList(List<MessZustand> meassuredStatusListinto){
     /*  for(int i = 0;i<meassuredStatusListinto.size();i++){
            if(meassuredStatusListinto.get(i).periodDuration>15){
                meassuredStatusListinto.remove(i);
            }
        }*/
       /* Collections.sort(meassuredStatusListinto, new Comparator<MessZustand>(){
            public int compare(MessZustand o1, MessZustand o2){
                if(o1.differenceInMeassuremdecharges == o2.differenceInMeassuremdecharges)
                    return 0;
                return o1.differenceInMeassuremdecharges < o2.differenceInMeassuremdecharges ? -1 : 1;
            }
        });

        while(meassuredStatusListinto.size()>100){
            meassuredStatusListinto.remove(meassuredStatusListinto.size()-1);
        }
*/
        Collections.sort(meassuredStatusListinto, new Comparator<MessZustand>(){
            public int compare(MessZustand o1, MessZustand o2){
                if(o1.meassuredValueLDR == o2.meassuredValueLDR)
                    return 0;
                return o1.meassuredValueLDR < o2.meassuredValueLDR ? -1 : 1;
            }
        });

        //Gucken wo groesster Abstand, dann kopieren und einfuegen wenn List unter 100

        while(meassuredStatusListinto.size()<100){
            double biggestGap=0;
            int index=0;
            for(int i = 0 ; i<meassuredStatusListinto.size()-1;i++){
                double currdechargeGap = meassuredStatusListinto.get(i+1).meassuredValueLDR-meassuredStatusListinto.get(i).meassuredValueLDR;
                if(currdechargeGap>biggestGap){
                    biggestGap=currdechargeGap;
                    index=i;
                }
            }
            meassuredStatusListinto.add(index+1, meassuredStatusListinto.get(index));
        }
        //Gucken wo kleinster Abstand, dann loeschen wenn List ueber 100



        while(meassuredStatusListinto.size()>100){

            double smallestGap=100;
            int index=0;
            for(int i = 0 ; i<meassuredStatusListinto.size()-1;i++){
                if(meassuredStatusListinto.get(i+1).meassuredValueLDR-meassuredStatusListinto.get(i).meassuredValueLDR<smallestGap){
                    index=i;
                }
            }
            meassuredStatusListinto.remove(index);
        }

        return meassuredStatusListinto;
    }
    public static void saveCalibratedLists(){
        try(BufferedWriter writer=new BufferedWriter(new FileWriter("geparsteListn.txt"))) {

            for(MessZustand m : getCalibratedMeassureListForRed()){
                writer.write("\nred;charge:"+m.chargeTime+";decharge:"+m.dechargeTime+";LDR:"+m.meassuredValueLDR+";perd:"+m.periodDuration+";diffv:"+m.differenceInMeassuremdecharges+";\n");
            }
            for(MessZustand m : getCalibratedMeassureListForBlue()){
                writer.write("\nBlue;charge:"+m.chargeTime+";decharge:"+m.dechargeTime+";LDR:"+m.meassuredValueLDR+";perd:"+m.periodDuration+";diffv:"+m.differenceInMeassuremdecharges+";\n");
            }
            for(MessZustand m : getCalibratedMeassureListForGreen()){
                writer.write("\ngreen;charge:"+m.chargeTime+";decharge:"+m.dechargeTime+";LDR:"+m.meassuredValueLDR+";perd:"+m.periodDuration+";diffv:"+m.differenceInMeassuremdecharges+";");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static MessZustand parseLineToMessZustand(String into){
         MessZustand out= new MessZustand();
         into=into.substring(into.indexOf(";")+1);
        out.chargeTime=Integer.parseInt(into.substring(into.indexOf(":", 0),into.indexOf(";", 0)).replace(":", ""));
        into=into.substring(into.indexOf(";")+1);
        out.dechargeTime=Integer.parseInt(into.substring(into.indexOf(":", 0),into.indexOf(";", 0)).replace(":", ""));
        into=into.substring(into.indexOf(";")+1);
        out.meassuredValueLDR=Double.parseDouble(into.substring(into.indexOf(":", 0),into.indexOf(";", 0)).replace(":", ""));
        into=into.substring(into.indexOf(";")+1);
        out.periodDuration=Integer.parseInt(into.substring(into.indexOf(":", 0),into.indexOf(";", 0)).replace(":", ""));
        into=into.substring(into.indexOf(";")+1);
        out.differenceInMeassuremdecharges=Double.parseDouble(into.substring(into.indexOf(":", 0),into.indexOf(";", 0)).replace(":", ""));
        return out;
    }
    public static void readCalibratedLists(){
        String currdechargeLine="";
        calibratedCalculateListForRed = new ArrayList<>();
        calibratedCalculateListForBlue = new ArrayList<>();
        calibratedCalculateListForGreen = new ArrayList<>();
         try(Scanner scan= new Scanner(new File("geparsteListn.txt"))){

             while(scan.hasNextLine()){
                 currdechargeLine=scan.nextLine();
                 if(currdechargeLine.startsWith("red;")){
                     getCalibratedMeassureListForRed().add(parseLineToMessZustand(currdechargeLine));
                 }
                 if(currdechargeLine.startsWith("Blue;")){
                     getCalibratedMeassureListForBlue().add(parseLineToMessZustand(currdechargeLine));
                 }
                 if(currdechargeLine.startsWith("green;")){
                     getCalibratedMeassureListForGreen().add(parseLineToMessZustand(currdechargeLine));
                 }
             }
         }catch (Exception e){
             e.printStackTrace();
         }
  }


    public static void checkPinsAndEnd(){
        red01.reset();
        green06.reset();
        blue05.reset();
    }

    //Sonstige
    public static void fillDefaultListRed(){
         defaultListForRed.add(new MessZustand(1,0));
         defaultListForRed.add(new MessZustand(0,0));
         defaultListForRed.add(new MessZustand(0,1));
         defaultListForRed.add(new MessZustand(0,2));
         defaultListForRed.add(new MessZustand(0,3));
         defaultListForRed.add(new MessZustand(0,4));
         defaultListForRed.add(new MessZustand(0,5));
         defaultListForRed.add(new MessZustand(0,6));
         defaultListForRed.add(new MessZustand(0,7));
         defaultListForRed.add(new MessZustand(0,8));
         defaultListForRed.add(new MessZustand(0,9));
         defaultListForRed.add(new MessZustand(0,10));
         defaultListForRed.add(new MessZustand(0,11));
         defaultListForRed.add(new MessZustand(0,12));
         defaultListForRed.add(new MessZustand(0,13));
    }
    public static void fillDefaultListBlueAndGreen(){

         defaultListForBlue.add(new MessZustand(9,4));
         defaultListForBlue.add(new MessZustand(8,4));
         defaultListForBlue.add(new MessZustand(7,4));
         defaultListForBlue.add(new MessZustand(6,4));
         defaultListForBlue.add(new MessZustand(5,4));
         defaultListForBlue.add(new MessZustand(4,4));
         defaultListForBlue.add(new MessZustand(3,4));
         defaultListForBlue.add(new MessZustand(2,4));
         defaultListForBlue.add(new MessZustand(1,4));
         defaultListForBlue.add(new MessZustand(0,4));
         defaultListForBlue.add(new MessZustand(0,6));
         defaultListForBlue.add(new MessZustand(0,8));
         defaultListForBlue.add(new MessZustand(0,11));
         defaultListForBlue.add(new MessZustand(0,13));
         defaultListForBlue.add(new MessZustand(0,15));
         defaultListForBlue.add(new MessZustand(0,18));
         defaultListForBlue.add(new MessZustand(0,20));

        defaultListForGreen.add(new MessZustand(7,6));
        defaultListForGreen.add(new MessZustand(6,7));
        defaultListForGreen.add(new MessZustand(5,7));
        defaultListForGreen.add(new MessZustand(3,7));
        defaultListForGreen.add(new MessZustand(2,7));
        defaultListForGreen.add(new MessZustand(1,7));
        defaultListForGreen.add(new MessZustand(1,9));
        defaultListForGreen.add(new MessZustand(1,12));
        defaultListForGreen.add(new MessZustand(0,3));
        defaultListForGreen.add(new MessZustand(0,5));
        defaultListForGreen.add(new MessZustand(0,7));
        defaultListForGreen.add(new MessZustand(0,9));
        defaultListForGreen.add(new MessZustand(0,11));
        defaultListForGreen.add(new MessZustand(0,13));
        defaultListForGreen.add(new MessZustand(0,16));
        defaultListForGreen.add(new MessZustand(0,19));
    }

    // Getter Setter
    public static List<MessZustand> getCalibratedMeassureListForRed() {
        synchronized (lockRed) {
        return calibratedCalculateListForRed;
    }
     }

    public static void setCalibratedCalculateList(int pinNumber, List<MessZustand> kalibrierteMessList) {
         switch (pinNumber){
             case 1:
                 synchronized (lockRed) {
                     PinHub.calibratedCalculateListForRed = kalibrierteMessList;
                 }
                 break;

             case 5:
                 synchronized (lockBlue) {
                     PinHub.calibratedCalculateListForBlue = kalibrierteMessList;
                 }
                 break;

             case 6: synchronized (lockGreen) {
                 PinHub.calibratedCalculateListForGreen = kalibrierteMessList;
             }

                 break;

         }

    }

    public static List<MessZustand> getCalibratedMeassureListForBlue() {
        synchronized (lockBlue) {
            return calibratedCalculateListForBlue;
        }
    }

    public static void setCalibratedCalculateListForBlue(List<MessZustand> calibratedCalculateListForBlue) {

            synchronized (lockBlue) {
                PinHub.calibratedCalculateListForBlue = calibratedCalculateListForBlue;
            }
    }
    public static List<MessZustand> getCalibratedMeassureListForGreen() {
        synchronized (lockGreen) {
            return calibratedCalculateListForGreen;
        }
    }

    public static void setCalibratedCalculateListForGreen(List<MessZustand> calibratedCalculateListForGreen) {
            synchronized (lockGreen) {

                PinHub.calibratedCalculateListForGreen = calibratedCalculateListForGreen;
            }
        }
}
