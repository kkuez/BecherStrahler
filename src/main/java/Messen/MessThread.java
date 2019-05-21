package Messen;

import Hardware.Pin;
import Hardware.PinHub;

import java.lang.Thread;
import java.util.*;

public class MessThread extends Thread {

    MessProcess messProcess;
    List<MessZustand> messZustandsList=new ArrayList<>();

    public MessThread(){
         messProcess= new MessProcess();
         messProcess.start();
    }

public double getOnce(MessProcess messProcess, int pin){
    double returnOut=0;
                        while(returnOut==0){
                            try {//Line 1 ist der LDR
                                returnOut = Double.parseDouble(messProcess.getMeassureLinesForEveryInput().get(1).substring(messProcess.getMeassureLinesForEveryInput().get(1).indexOf("(") + 1, messProcess.getMeassureLinesForEveryInput().get(1).indexOf(".") + 4));
                            }catch (Exception e){
                                System.out.println("Fehler beim Parsen von Pin 0"+pin);
                                e.printStackTrace();
                            }
                            }
                            messProcess.getMeassureLinesForEveryInput().set(pin, "0");
    return returnOut;
}
public void calibrate(int pinnumber){
    Pin currentPin=new Pin(pinnumber);
    switch (pinnumber){
        case 1:
            currentPin= PinHub.red01;
            break;
        case 5:
            currentPin= PinHub.blue05;
            break;
        case 6:
            currentPin= PinHub.green06;
            break;

    }

    System.out.println("Warte charge erste komplette Messung...");

    while(messProcess.getMeassureLinesForEveryInput().size()<8){

    }
    double meassureM=0;
    double meassureMin=0;
    double meassureDiff;
    double meassureAverageValue;
    double meassureCurrentValue=0;
    double[] arrayOfAllMeassureValues=new double[20];

  for(int i =0;i<20;i++) {
        for (int a = 0; a < 20; a++) {
            if(a+i<23) {
                for (int b = 0; b < 10; b++) {      //10 Messungen um Differenz out zuziehen

                    meassureCurrentValue = getOnce(messProcess, pinnumber);
                    arrayOfAllMeassureValues[b] = meassureCurrentValue;
                    try {
                        sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }

                Arrays.sort(arrayOfAllMeassureValues);
                double[] filteredArray = new double[8];
                for (int c = 0; c < 8; c++) {
                    filteredArray[c] = arrayOfAllMeassureValues[c + 2];
                }

                for (double d : filteredArray) {
                    if (d == 0) {
                        meassureM = meassureCurrentValue;
                        meassureMin = meassureCurrentValue;
                    } else {
                        if (meassureCurrentValue < meassureMin) {
                            meassureMin = meassureCurrentValue;
                        }
                        if (meassureCurrentValue > meassureM) {
                            meassureM = meassureCurrentValue;
                        }
                    }
                }

                meassureDiff = meassureM - meassureMin;
                MessZustand messZustand = new MessZustand();
                currentPin.pinSet(i, a);
                messZustand = new MessZustand(i, a, meassureM, meassureDiff);

                if (messZustand.meassuredValueLDR != 0) {
                    messZustandsList.add(messZustand);
                }
            }
        }
    }

    System.out.println("Messung beendet\nVerarbeite List...");
    messZustandsList= PinHub.processList(messZustandsList);
    PinHub.setCalibratedCalculateList(pinnumber,messZustandsList);
    PinHub.checkPinsAndEnd();
}





    public void run(){
       calibrate(1);
       calibrate(5);
       calibrate(6);
       PinHub.saveCalibratedLists();
        messProcess=null;
       System.out.println("Kalibrierung beendet");

    }
}
