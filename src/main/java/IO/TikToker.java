package IO;

import java.lang.Thread;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TikToker extends Thread{
    IO io ;
            public TikToker(IO iorein){
        io=iorein;

            }
    List<TikTok> tiktokListe = new ArrayList<>();
            TikTok tiktokZumFuellen=new TikTok(true, Calendar.getInstance().getTimeInMillis()) ;





    @Override
    public void run(){

        tiktokListe.add(tiktokZumFuellen);
        tiktokListe.add(tiktokZumFuellen);
        while(true){
            try {
                sleep(5000);
                io.getMsgList().add("tok");
                if((Calendar.getInstance().getTimeInMillis()-tiktokListe.get(0).zeitPunktInMillis)>5000||tiktokListe.get(0).gueltig==false){
                    setTiktokListe(new TikTok(false,Calendar.getInstance().getTimeInMillis()));

                }
                if(tiktokListe.size()>3){
                    tiktokListe.remove(3);
                }
                if(!tiktokListe.get(0).gueltig&&!tiktokListe.get(1).gueltig&&!tiktokListe.get(2).gueltig){
                    io.isConnected=false;
                    System.out.println("Verbindung verloren.");
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setTiktokListe(TikTok rein){
        tiktokListe.add(0,rein);
    }
}

