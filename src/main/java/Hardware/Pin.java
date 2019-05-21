package Hardware;

import jwiringpi.*;

public class Pin extends Thread {
    final int HIGH = 1;
    final int LOW = 0;
    final int OUTPUT = 1;
    public int pinNumber=0;
    int chargeTime;
    int dechargeTime;
    boolean blink;
	
    boolean reset=true;
	int consoleMode=0;
    Object lockCharge= new Object();
    Object lockDecharge= new Object();
    Object lockRes= new Object();
    Object lockVolt= new Object();
	String voltage="0";
    JWiringPiController gpio = new JWiringPiController();

    public Pin(int pinNumberIncoming){
        pinNumber=pinNumberIncoming;
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
    public synchronized void pinSet(int charge,  int decharge){
                    setReset(false);
                    setChargeTime(charge);
                    setDechargeTime(decharge);

    }
    public void directLow(){
        gpio.digitalWrite(pinNumber, LOW);
    }
	
    public void directHigh(){
        gpio.digitalWrite(pinNumber, HIGH);
    }

    public void resetten(){
        setReset(true);
       gpio.digitalWrite(pinNumber, LOW);
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

        if(blink){
            int counter=0;
            boolean getsStronger=true;
            while(!reset) {
                counter++;
                System.out.println("Neuer Zkylus, "+counter);
                gpio.digitalWrite(pinNumber, HIGH);
                if(getsStronger ){
                    chargeTime++;
                }else{
                    chargeTime--;
                }
                gpio.delay(chargeTime);
                if(chargeTime>20){
                    getsStronger=false;
                }
                if(chargeTime<3){
                    getsStronger=true;
                }
                gpio.digitalWrite(pinNumber, LOW);
                gpio.delay(dechargeTime);//5ms sind ein guter value
            }
        }else{
            while(!reset){
                gpio.digitalWrite(pinNumber,LOW);
                gpio.delay(getChargeTime());
                gpio.digitalWrite(pinNumber,HIGH);
                gpio.delay(getDechargeTime());//5ms sind ein guter Wert
            }
        }
    }

    public void refresh(){
                switch (pinNumber){
                    case 1:
                        chargeTime=PinHub.red1ChargeTime;
                        dechargeTime=PinHub.red1DechargeTime;
                        voltage=PinHub.red1Voltage;
                        break;
                    case 5:
                        chargeTime=PinHub.blue5ChargeTime;
                        dechargeTime=PinHub.blue5DechargeTime;
                        voltage=PinHub.blue5Voltage;
                        break;
                    case 6:
                        chargeTime=PinHub.green6ChargeTime;
                        dechargeTime=PinHub.green6DechargeTime;
                        voltage=PinHub.green6Voltage;
                        break;
                    default:
                        break;
        }
    }
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
    public String getVoltage() {
        synchronized (lockVolt) {
            return voltage;
        }
    }

    public void setVoltage(String voltage) {
        synchronized (lockVolt) {
            this.voltage = voltage;
        }
    }
}
