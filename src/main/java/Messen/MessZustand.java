package Messen;

public class MessZustand   {
    public int chargeTime;
    public int dechargeTime;
    public double meassuredValueLDR;
    public int periodDuration;
    public double differenceInMeassuremdecharges;

    public MessZustand(){}
    public MessZustand(int chargeTime, int dechargeTime){
        this.chargeTime=chargeTime;
        this.dechargeTime=dechargeTime;
    }

    public MessZustand( int chargeTime, int dechargeTime, double meassuredValueLDR,double differenceInMeassuremdecharges){
        this.chargeTime=chargeTime;
        this.dechargeTime=dechargeTime;
        this.meassuredValueLDR=meassuredValueLDR;
        this.differenceInMeassuremdecharges=differenceInMeassuremdecharges;
        periodDuration=chargeTime+dechargeTime;

    }


}
