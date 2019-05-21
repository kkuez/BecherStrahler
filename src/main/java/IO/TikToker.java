package IO;

import java.lang.Thread;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TikToker extends Thread{
    IO io ;
            public TikToker(IO iointo){
        io=iointo;

            }
    List<TikTok> tiktokList = new ArrayList<>();
            TikTok tiktokToFill=new TikTok(true, Calendar.getInstance().getTimeInMillis()) ;





    @Override
    public void run(){

        tiktokList.add(tiktokToFill);
        tiktokList.add(tiktokToFill);
        while(true){
            try {
                sleep(5000);
                io.getMsgList().add("tok");
                if((Calendar.getInstance().getTimeInMillis()-tiktokList.get(0).pointOfTimeInMillis)>5000||tiktokList.get(0).valid==false){
                    setTiktokList(new TikTok(false,Calendar.getInstance().getTimeInMillis()));

                }
                if(tiktokList.size()>3){
                    tiktokList.remove(3);
                }
                if(!tiktokList.get(0).valid&&!tiktokList.get(1).valid&&!tiktokList.get(2).valid){
                    io.isConnected=false;
                    System.out.println("Verbindung verloren.");
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setTiktokList(TikTok into){
        tiktokList.add(0,into);
    }
}

