

public class MessZustand   {
    public int aufladezeit;
    public int entladezeit;
    public double messwertLDR;
    public int periodendauer;
    double differenzInMessungen;

    public MessZustand(){}
    public MessZustand(int aufladezeit, int entladezeit){
        this.aufladezeit=aufladezeit;
        this.entladezeit=entladezeit;
    }

    public MessZustand( int aufladezeit, int entladezeit, double messwertLDR,double differenzInMessungen){
        this.aufladezeit=aufladezeit;
        this.entladezeit=entladezeit;
        this.messwertLDR=messwertLDR;
        this.differenzInMessungen=differenzInMessungen;
        periodendauer=aufladezeit+entladezeit;

    }


}
