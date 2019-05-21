package Hardware;

import jwiringpi.*;

public class Pin extends Thread {
    final int HIGH = 1;
    final int LOW = 0;
    final int OUTPUT = 1;
    int pinNumber=0;
    int chargeTime;
    int dechargeTime;
    public boolean blink;


    boolean reset=true;



    int consoleMode=0;
    Object lockCharge= new Object();
    Object lockDecharge= new Object();
    Object lockRes= new Object();
    Object lockVolt= new Object();


    String voltage="0";
    JWiringPiController gpio = new JWiringPiController();




    public Pin(int pinNumberinto){

        pinNumber=pinNumberinto;
        if (gpio.wiringPiSetup() < 0) {
            System.out.println("WiringPi setup error");
            return;
        }

        gpio.pinMode(pinNumber, OUTPUT);
        gpio.digitalWrite(pinNumber, HIGH);
    }

    public synchronized void pinSet(boolean value){
        blink=value;
    }
    public synchronized void pinSet(int chargeladen,  int dechargeladen){
                    setReset(false);
                    setChargeTime(chargeladen);

                    setDechargeTime(dechargeladen);

    }
    public void directLow(){
        gpio.digitalWrite(pinNumber, LOW);
    }
    public void directHigh(){
        gpio.digitalWrite(pinNumber, HIGH);
    }

    public void reset(){
        setReset(true);
       gpio.digitalWrite(pinNumber, LOW);
    }
    public void go(){
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

        if(blink){
            int counter=0;

            boolean becomesStronger=true;
            while(!reset) {
                counter++;
                System.out.println("Neuer Zkylus, "+counter);
                gpio.digitalWrite(pinNumber, HIGH);
                if(becomesStronger ){
                    chargeTime++;
                }else{
                    chargeTime--;
                }
                gpio.delay(chargeTime);
                if(chargeTime>20){
                    becomesStronger=false;
                }
                if(chargeTime<3){
                    becomesStronger=true;
                }
                gpio.digitalWrite(pinNumber, LOW);
                gpio.delay(dechargeTime);//5ms sind ein guter value
            }

        }else{
            while(!reset){


                gpio.digitalWrite(pinNumber,LOW);
                gpio.delay(getChargeTime());
                gpio.digitalWrite(pinNumber,HIGH);
                gpio.delay(getDechargeTime());//5ms sind ein guter Value
            }
        }
    }

    public void refresh(){
                switch (pinNumber){
                    case 1:
                        chargeTime=PinHub.red1ChargeladeZeit;
                        dechargeTime=PinHub.red1DechargeladeZeit;
                        voltage=PinHub.red1Voltnnung;
                        break;
                    case 5:
                        chargeTime=PinHub.Blue5ChargeladeZeit;
                        dechargeTime=PinHub.Blue5DechargeladeZeit;
                        voltage=PinHub.Blue5Voltnnung;
                        break;
                    case 6:
                        chargeTime=PinHub.green6ChargeladeZeit;
                        dechargeTime=PinHub.green6DechargeladeZeit;
                        voltage=PinHub.green6Voltnnung;
                        break;
                    default:
                        break;
        }
    }
	
    public void run( ) {
     //  refresh();
        while(true) {
            go();
        }

    }
	
//GETTER SETTER
    public synchronized int getConsoleMode() {
        return consoleMode;
    }

    public synchronized void setConsoleMode(int consoleMode) {
        this.consoleMode = consoleMode;
    }

    public int getChargeTime() {
        synchronized (lockCharge){
            return chargeTime;
        }

    }

    public void setChargeTime(int chargeTime) {
        synchronized (lockCharge) {
            this.chargeTime = chargeTime;
        }
    }

    public int getDechargeTime() {
        synchronized (lockDecharge) {
            return dechargeTime;
        }
    }

    public  void setDechargeTime(int dechargeTime) {
        synchronized (lockDecharge) {
            this.dechargeTime = dechargeTime;
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
    public String getVoltnnung() {
        synchronized (lockVolt) {
            return voltage;
        }
    }

    public void setVoltnnung(String voltage) {
        synchronized (lockVolt) {
            this.voltage = voltage;
        }
    }
}
