package Hardware;

import jwiringpi.*;

public class Pin extends Thread {
    public  final int HIGH = 1;
    public  final int LOW = 0;
    public  final int OUTPUT = 1;
    public  int pinNummer=0;
     int aufladezeit;
     int entladezeit;
    public boolean blinken;


    boolean reset=true;



    int consoleMode=0;
    Object lockAuf= new Object();
    Object lockEnt= new Object();
    Object lockRes= new Object();
    Object lockSpa= new Object();


    String spannung="0";
    JWiringPiController gpio = new JWiringPiController();




    public Pin(int pinNummerrein){

        pinNummer=pinNummerrein;
        if (gpio.wiringPiSetup() < 0) {
            System.out.println("WiringPi setup error");
            return;
        }

        gpio.pinMode(pinNummer, OUTPUT);
        gpio.digitalWrite(pinNummer, HIGH);
    }

    public synchronized void pinSetzen(boolean wert){
        blinken=wert;
    }
    public synchronized void pinSetzen(int aufladen,  int entladen){
                    setReset(false);
                    setAufladezeit(aufladen);

                    setEntladezeit(entladen);

    }
    public void directLow(){
        gpio.digitalWrite(pinNummer, LOW);
    }
    public void directHigh(){
        gpio.digitalWrite(pinNummer, HIGH);
    }

    public void resetten(){
        setReset(true);
       gpio.digitalWrite(pinNummer, LOW);
    }
    public void laufen(){
        switch (getConsoleMode()){

            case 0:

                break;
                case 1:
                    directLow();
                break;
                case 2:
                    directHigh();
                break;
                default:
                    break;
        }

        if(blinken){
            int zaehler=0;

            boolean wirdstaerker=true;
            while(!reset) {
                zaehler++;
                System.out.println("Neuer Zkylus, "+zaehler);
                gpio.digitalWrite(pinNummer, HIGH);
                if(wirdstaerker ){
                    aufladezeit++;
                }else{
                    aufladezeit--;
                }
                gpio.delay(aufladezeit);
                if(aufladezeit>20){
                    wirdstaerker=false;
                }
                if(aufladezeit<3){
                    wirdstaerker=true;
                }
                gpio.digitalWrite(pinNummer, LOW);
                gpio.delay(entladezeit);//5ms sind ein guter wert
            }

        }else{
            while(!reset){


                gpio.digitalWrite(pinNummer,LOW);
                gpio.delay(getAufladezeit());
                gpio.digitalWrite(pinNummer,HIGH);
                gpio.delay(getEntladezeit());//5ms sind ein guter Wert
            }
        }
    }

    /*public void refresh(){
                switch (pinNummer){
                    case 1:
                        aufladezeit=PinHub.rot1AufladeZeit;
                        entladezeit=PinHub.rot1EntladeZeit;
                        spannung=PinHub.ro1Spannung;
                        break;
                    case 5:
                        aufladezeit=PinHub.blau5AufladeZeit;
                        entladezeit=PinHub.blau5EntladeZeit;
                        spannung=PinHub.blau5Spannung;
                        break;
                    case 6:
                        aufladezeit=PinHub.gruen6AufladeZeit;
                        entladezeit=PinHub.gruen6EntladeZeit;
                        spannung=PinHub.gruen6Spannung;
                        break;
                    default:
                        break;
        }
    }*/
    public void run( ) {
     //  refresh();
        while(true) {
            laufen();
        }

    }
//GETTER SETTER

    public synchronized int getConsoleMode() {
        return consoleMode;
    }

    public synchronized void setConsoleMode(int consoleMode) {
        this.consoleMode = consoleMode;
    }

    public int getAufladezeit() {
        synchronized (lockAuf){

            return aufladezeit;
        }

    }

    public void setAufladezeit(int aufladezeit) {
        synchronized (lockAuf) {
            this.aufladezeit = aufladezeit;
        }
    }

    public int getEntladezeit() {
        synchronized (lockEnt) {
            return entladezeit;
        }
    }

    public  void setEntladezeit(int entladezeit) {
        synchronized (lockEnt) {
            this.entladezeit = entladezeit;
        }
    }

    public boolean getReset() {
        synchronized (lockRes) {
            return reset;
        }
    }

    public void setReset(boolean reset) {
        synchronized (lockRes) {
            this.reset = reset;
        }
    }
    public String getSpannung() {
        synchronized (lockSpa) {
            return spannung;
        }
    }

    public void setSpannung(String spannung) {
        synchronized (lockSpa) {
            this.spannung = spannung;
        }
    }
}
